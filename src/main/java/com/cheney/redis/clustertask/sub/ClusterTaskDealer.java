package com.cheney.redis.clustertask.sub;

import com.alibaba.fastjson.JSON;
import com.cheney.redis.RedisEval;
import com.cheney.redis.clustertask.TaskConfig;
import com.cheney.redis.clustertask.TaskInfo;
import com.cheney.system.page.Limit;
import com.cheney.utils.ResultAndFlag;
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
import static com.cheney.redis.clustertask.pub.ClusterTaskPublisher.CLUSTER_TASK_PRE_KEY;

/**
 * 集群任务处理器
 *
 * @author cheney
 * @date 2019-09-03
 */
@Component
@Slf4j
public class ClusterTaskDealer implements RedisEval {

    private RedisTemplate<String, Object> redisTemplate;

    private ExecutorService taskExecutor;

    public ClusterTaskDealer(@Qualifier("strRedisTemplate") RedisTemplate<String, Object> redisTemplate, @Qualifier("clusterTaskExecutor") ExecutorService taskExecutor) {
        this.redisTemplate = redisTemplate;
        this.taskExecutor = taskExecutor;
    }

    public void dealTask(String taskId, int concurrentNums, ClusterTaskSubscriber subscriber) {

        // 重置活动状态
        subscriber.resetActive();
        // 选取主节点
        final boolean[] master = {false};

        TaskInfo taskInfo = getTaskInfo(taskId);

        if (!taskInfo.isValid()) {
            return;
        }

        // 统计线程执行
        CountDownLatch taskCountDownLatch = new CountDownLatch(concurrentNums);

        List<Callable<String>> tasks = new ArrayList<>();
        for (int i = 0; i < concurrentNums; i++) {
            Callable<String> task = () -> {
                try {
                    ResultAndFlag<Limit> limitResult;
                    while (subscriber.isActive() && (limitResult = getLimit(taskInfo)).isSuccess()) {
                        Limit limit = limitResult.getResult();
                        log.info("【集群任务】开始执行集群任务，数据总数:{},limit:{}", taskInfo.getDataNums(), JSON.toJSONString(limit));
                        try {
                            subscriber.execute(taskInfo, limit);
                        } catch (Exception e) {
                            log.error("【集群任务】执行线程任务limit:{}-{}异常:{}", limit.getNum(), limit.getSize(), e);
                        }
                    }
                    // after all tasks finished
                    subscriber.stop();
                } catch (Throwable t) {
                    log.error("【集群任务】任务线程执行异常", t);
                } finally {
                    // 成功删除的节点视为主节点
                    Boolean delete = redisTemplate.delete(CLUSTER_TASK_PRE_KEY + taskId);
                    if (delete != null && delete) {
                        master[0] = true;
                    }
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
            // 完成线程任务回调
            taskCountDownLatch.await();

             // todo 暂时所有节点都执行回调
             //  （CountDownLatch只能保证单台服务器所有线程任务执行完毕）
             //  （由于是分布式，没有做注册中心无法发现一共有几个服务正在执行，所以无法得知是否所有服务都执行完线程）
//            if (master[0]) {
                // 只有主节点执行回调
                subscriber.afterAllTask();
//            }
        } catch (Exception e) {
            log.error("【集群任务】执行afterAllTask报错", e);
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
    private ResultAndFlag<Limit> getLimit(TaskInfo taskInfo) {
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
                return ResultAndFlag.fail();
            }
            stepCount = (long) executeResult;
        } catch (Exception e) {
            log.error("【集群任务】执行lua脚本异常", e);
            return ResultAndFlag.fail();
        }
        // 分页开始数
        int startNum = (int) (stepCount * stepSize);
        if (startNum >= dataNums) {
            // 已经执行完毕
            return ResultAndFlag.fail();
        } else if (startNum + stepSize >= dataNums) {
            // 最后一步
            stepSize = dataNums - startNum;
        } else {
            // 延长过期时间
            extendedExpire(taskRedisKey);
        }

        if (taskInfo.isDesc()) {
            // 倒序
            startNum = dataNums - startNum - stepSize;
        }
        return ResultAndFlag.success(Limit.create(startNum, stepSize));
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
