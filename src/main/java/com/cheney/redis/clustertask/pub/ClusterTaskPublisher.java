package com.cheney.redis.clustertask.pub;

import com.cheney.javaconfig.redis.RedisKey;

/**
 * 集群任务发布
 *
 * @author cheney
 * @date 2019-09-03
 */
public interface ClusterTaskPublisher {

    /**
     * 集群任务redis订阅发布channel
     */
    public final String CLUSTER_TASK_CHANNEL_PRE_KEY = RedisKey.CLUSTER_TASK_CHANNEL_PRE_KEY.getKey();

    /**
     * 集群任务redis任务信息key
     */
    public final String CLUSTER_TASK_PRE_KEY = RedisKey.CLUSTER_TASK_PRE_KEY.getKey();

    /**
     * 发布集群任务
     *
     * @param taskId         任务ID
     * @param dataNums       待处理数据个数
     * @param stepSize       步长
     * @param concurrentNums 单个服务器并发数量
     */
    void publish(String taskId, int dataNums, int stepSize, int concurrentNums);

}
