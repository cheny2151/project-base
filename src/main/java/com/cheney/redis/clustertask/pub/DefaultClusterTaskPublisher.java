package com.cheney.redis.clustertask.pub;

import com.cheney.redis.clustertask.TaskInfo;
import com.cheney.redis.clustertask.sub.test.TestSub;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

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
    public void publish(String taskId, int dataNums, int stepSize, int concurrentNums) {

        String taskIdKey = CLUSTER_TASK_PRE_KEY + taskId;
        Boolean exists = redisTemplate.hasKey(taskIdKey);
        if (exists != null && exists) {
            log.info("任务taskId:{}未结束，无法分配新任务", taskId);
            return;
        }
        // set taskInfo
        Map<String, String> taskInfo = new TaskInfo(taskIdKey, dataNums, 0, stepSize);
        redisTemplate.opsForHash().putAll(taskIdKey, taskInfo);

        // pub task
        redisTemplate.convertAndSend(CLUSTER_TASK_CHANNEL_PRE_KEY + taskIdKey, String.valueOf(concurrentNums));
    }

}
