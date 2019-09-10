package com.cheney.redis.clustertask.pub;

import com.cheney.redis.clustertask.TaskConfig;
import com.cheney.redis.clustertask.TaskInfo;
import com.cheney.redis.clustertask.sub.test.TestSub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 默认集群任务发布器
 * (任务订阅用例{@link TestSub})
 *
 * @author cheney
 * @date 2019-09-03
 */
@Slf4j
@Component
public class DefaultClusterTaskPublisher implements ClusterTaskPublisher {

    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public DefaultClusterTaskPublisher(@Qualifier("strRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void publish(String taskId, int dataNums, int stepSize, int concurrentNums, boolean desc) {

        String taskRedisKey = CLUSTER_TASK_PRE_KEY + taskId;
        Boolean exists = redisTemplate.hasKey(taskRedisKey);
        if (exists != null && exists) {
            log.info("【集群任务】任务taskId:{}未结束，无法分配新任务", taskId);
            return;
        }
        // set taskInfo
        Map<String, String> taskInfo = new TaskInfo(taskId, dataNums, 0, stepSize, desc);
        redisTemplate.opsForHash().putAll(taskRedisKey, taskInfo);
        // 设置过期时间，防止所有线程终止，无法删除任务标识
        redisTemplate.expire(taskRedisKey, TaskConfig.KEY_EXPIRE_SECONDS, TimeUnit.SECONDS);

        // pub task
        log.info("【集群任务】发布集群任务：taskId->{},数量->{}", taskId, dataNums);
        redisTemplate.convertAndSend(CLUSTER_TASK_CHANNEL_PRE_KEY + taskId, String.valueOf(concurrentNums));
    }

}
