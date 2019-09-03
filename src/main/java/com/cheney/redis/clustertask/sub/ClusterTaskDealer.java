package com.cheney.redis.clustertask.sub;

import com.alibaba.fastjson.JSON;
import com.cheney.redis.RedisEval;
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
import java.util.concurrent.ExecutorService;

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

        TaskInfo taskInfo = getTaskInfo(taskId);

        if (!taskInfo.isValid()) {
            return;
        }

        List<Callable<String>> tasks = new ArrayList<>();
        for (int i = 0; i <= concurrentNums; i++) {
            Callable<String> task = () -> {
                ResultAndFlag<Limit> limit;
                while ((limit = getLimit(taskInfo)).isSuccess()) {
                    log.info("开始执行集群任务，数据总数:{},limit:{}", taskInfo.getDataNums(), JSON.toJSONString(limit.getResult()));
                    subscriber.execute(taskInfo, limit.getResult());
                }
                redisTemplate.delete(CLUSTER_TASK_PRE_KEY + taskId);
                return "success";
            };
            tasks.add(task);
        }

        try {
            taskExecutor.invokeAll(tasks);
        } catch (InterruptedException e) {
            log.error("集群任务线程中断", e);
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
        keys.add(CLUSTER_TASK_PRE_KEY + taskId);
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
            log.error("执行lua脚本异常", e);
            return ResultAndFlag.fail();
        }
        // 分页开始数
        int startNum = (int) (stepCount * stepSize);
        if (startNum >= dataNums) {
            // 已经执行完毕
            return ResultAndFlag.fail();
        } else if (startNum + stepSize >= dataNums) {
            // 最后一步大于总数
            stepSize = dataNums - startNum;
        }
        return ResultAndFlag.success(Limit.create(startNum, stepSize));
    }
}
