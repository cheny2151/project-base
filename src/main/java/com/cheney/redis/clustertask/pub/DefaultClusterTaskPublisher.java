package com.cheney.redis.clustertask.pub;

import com.cheney.redis.clustertask.TaskInfo;
import com.cheney.redis.clustertask.sub.test.TestSub;
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
@Component
public class DefaultClusterTaskPublisher implements ClusterTaskPublisher {

    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    public DefaultClusterTaskPublisher(@Qualifier("strRedisTemplate") RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public void publish(String taskId, int dataNums, int stepSize, int concurrentNums) {

        // set taskInfo
        Map<String, String> taskInfo = new TaskInfo(taskId, dataNums, 0, stepSize);
        redisTemplate.opsForHash().putAll(CLUSTER_TASK_PRE_KEY + taskId, taskInfo);

        // pub task
        redisTemplate.convertAndSend(CLUSTER_TASK_CHANNEL_PRE_KEY + taskId, String.valueOf(concurrentNums));
    }

}
