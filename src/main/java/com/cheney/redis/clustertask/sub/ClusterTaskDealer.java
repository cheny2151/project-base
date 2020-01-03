package com.cheney.redis.clustertask.sub;

import com.cheney.redis.RedisEval;
import com.cheney.redis.clustertask.TaskConfig;
import com.cheney.redis.clustertask.TaskInfo;
import com.cheney.system.page.Limit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.cheney.redis.clustertask.TaskLuaScript.ADD_STEP_LUA_SCRIPT;
import static com.cheney.redis.clustertask.TaskLuaScript.REGISTERED_LUA_SCRIPT;
import static com.cheney.redis.clustertask.pub.ClusterTaskPublisher.CLUSTER_TASK_PRE_KEY;

/**
 * 集群任务处理器
 * 负责集群任务的分页的获取，线程任务的分配与执行，
 * 任务执行结束后的回调。
 *
 * v1.1.0 更新：执行任务完成回调函数afterAllTask()变更：
 *             订阅到任务时通过redis脚本注册执行任务的分布式服务器数,
 *             每个服务器执行完毕任务时再减少注册数，直至0视为所有任务执行完毕,
 *             此时执行清除redis任务数据,回调afterAllTask()。
 *             见{@link ClusterTaskDealer#registeredTask(String taskId, int concurrentNums, ClusterTaskSubscriber subscriber)}
 *
 * @author cheney
 * @date 2019-09-03
 * @version 1.1.0
 */
@Component
@Slf4j
public class ClusterTaskDealer implements RedisEval {

    // 服务器注册执行任务标识
    private final static String REGISTERED_LABEL = "REGISTERED_COUNT";

    private RedisTemplate<String, Object> redisTemplate;

    private ExecutorService taskExecutor;

    public ClusterTaskDealer(@Qualifier("redisTemplate") RedisTemplate<String, Object> redisTemplate,
                             @Qualifier("clusterTaskExecutor") ExecutorService taskExecutor) {
        this.redisTemplate = redisTemplate;
        this.taskExecutor = taskExecutor;
    }


    /**
     * 注册任务
     * v1.1.0新增
     *
     * @param taskId         任务id
     * @param concurrentNums 任务线程数
     * @param subscriber     订阅者
     */
    public void registeredTask(String taskId, int concurrentNums, ClusterTaskSubscriber subscriber) {

        List<String> keys = new ArrayList<>();
        String fullKey = CLUSTER_TASK_PRE_KEY + taskId;
        keys.add(fullKey);
        keys.add(REGISTERED_LABEL);

        long time = System.currentTimeMillis();
        try {
            // 执行lua脚本注册任务 v1.1.0
            execute(redisTemplate, REGISTERED_LUA_SCRIPT, keys, Collections.singletonList("1"));
            this.distributionTask(taskId, concurrentNums, subscriber);
            log.info("【集群任务】任务taskId:{},本机执行完毕,本机耗时:{}秒", taskId, (System.currentTimeMillis() - time) / 1000);
        } finally {
            // 执行lua脚本获取当前剩余注册数
            Object RemainingNum = execute(redisTemplate, REGISTERED_LUA_SCRIPT, keys, Collections.singletonList("-1"));
            if ((long) RemainingNum == 0) {
                log.info("【集群任务】任务taskId:{},所有服务器执行完毕", taskId);
                // 清除任务
                redisTemplate.delete(fullKey);
                // 执行任务完成回调
                subscriber.afterAllTask();
            }
        }

    }

    /**
     * 分配任务
     */
    private void distributionTask(String taskId, int concurrentNums, ClusterTaskSubscriber subscriber) {

        // 重置活动状态
        subscriber.resetActive();

        TaskInfo taskInfo = getTaskInfo(taskId);

        if (!taskInfo.isValid()) {
            log.info("【集群任务】无需执行任务,所有分页已被其他节点消费");
            return;
        }

        // 统计线程执行
        CountDownLatch taskCountDownLatch = new CountDownLatch(concurrentNums);

        List<Callable<String>> tasks = new ArrayList<>();
        for (int i = 0; i < concurrentNums; i++) {
            Callable<String> task = () -> {
                try {
                    LimitResult limitResult;
                    while (subscriber.isActive() && (limitResult = getLimit(taskInfo)).isSuccess()) {
                        Limit limit = limitResult.getResult();
                        log.info("【集群任务】开始执行集群任务,ID:'{}'，数据总数:{},limit:{{},{}}", taskId, taskInfo.getDataNums(), limit.getNum(), limit.getSize());
                        try {
                            subscriber.execute(taskInfo, limit);
                        } catch (Exception e) {
                            log.error("【集群任务】执行线程任务,ID:'{}'，limit:{{},{}}异常:{}", taskId, limit.getNum(), limit.getSize(), e);
                            subscriber.error(e);
                        }
                    }
                    // all tasks finished or stop
                    subscriber.stop();
                } catch (Throwable t) {
                    log.error("【集群任务】任务线程执行异常", t);
                } finally {
                    // count down信号量
                    taskCountDownLatch.countDown();
                }
                return "success";
            };
            tasks.add(task);
        }

        subscriber.before();
        try {
            // 开始执行线程
            taskExecutor.invokeAll(tasks);
        } catch (InterruptedException e) {
            log.error("【集群任务】集群任务线程中断", e);
        }

        try {
            // 主线程等待任务结束
            taskCountDownLatch.await();
        } catch (Exception e) {
            log.error("【集群任务】主线程等待任务执行报错", e);
        }

    }

    /**
     * 获取任务信息
     *
     * @param taskId 任务id
     * @return 任务信息实体
     */
    private TaskInfo getTaskInfo(String taskId) {
        HashOperations<String, String, String> hashOperations = redisTemplate.opsForHash();
        Map<String, String> taskInfo = hashOperations.entries(CLUSTER_TASK_PRE_KEY + taskId);
        return new TaskInfo(taskInfo);
    }

    /**
     * 获取当前线程任务处理数分页limit
     *
     * @param taskInfo 任务信息
     * @return 分页实体
     */
    private LimitResult getLimit(TaskInfo taskInfo) {
        String taskId = taskInfo.getTaskId();
        // 分页步长
        Integer stepSize = taskInfo.getStepSize();
        Integer dataNums = taskInfo.getDataNums();
        List<String> keys = new ArrayList<>();
        String taskRedisKey = CLUSTER_TASK_PRE_KEY + taskId;
        keys.add(taskRedisKey);
        keys.add("stepCount");
        long stepCount;
        try {
            // 执行lua脚本获取当前步长
            Object executeResult = execute(redisTemplate, ADD_STEP_LUA_SCRIPT, keys, Collections.emptyList());
            if (executeResult == null) {
                // 已经被其他线程删除
                return LimitResult.completed();
            }
            stepCount = (long) executeResult;
        } catch (Exception e) {
            log.error("【集群任务】执行lua脚本异常", e);
            return LimitResult.completed();
        }
        // 分页开始数
        int startNum = (int) (stepCount * stepSize);
        boolean last = false;
        if (startNum >= dataNums) {
            // 已经执行完毕
            return LimitResult.completed();
        } else if (startNum + stepSize >= dataNums) {
            // 最后一步
            stepSize = dataNums - startNum;
            last = true;
        } else {
            // 延长过期时间
            extendedExpire(taskRedisKey);
        }

        if (taskInfo.isDesc()) {
            // 倒序
            startNum = dataNums - startNum - stepSize;
        }
        return LimitResult.newLimit(Limit.create(startNum, stepSize), last);
    }

    /**
     * 延长key过期时间
     *
     * @param key 任务key
     */
    private void extendedExpire(String key) {
        redisTemplate.expire(key, TaskConfig.KEY_EXPIRE_SECONDS, TimeUnit.SECONDS);
    }

}