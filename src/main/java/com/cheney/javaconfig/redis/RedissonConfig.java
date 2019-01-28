package com.cheney.javaconfig.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;

/**
 * redisson配置
 */
//@Configuration
public class RedissonConfig {

    @Value("${spring.redis.cluster.nodes}")
    private String[] hostAndPort;
    @Value("${spring.redis.timeout}")
    private int timeout;

//    @Bean("redissonClient")
    public RedissonClient redissonClient() {
        Config config = new Config();
        ClusterServersConfig serversConfig = config.useClusterServers();
        for (int i = 0; i < hostAndPort.length; i++) {
            hostAndPort[i] = "redis://" + hostAndPort[i];
        }
        serversConfig.addNodeAddress(hostAndPort);
        serversConfig.setConnectTimeout(timeout);
        serversConfig.setMasterConnectionMinimumIdleSize(2);
        serversConfig.setMasterConnectionPoolSize(10);
        serversConfig.setSlaveConnectionMinimumIdleSize(3);
        serversConfig.setSlaveConnectionPoolSize(64);
        return Redisson.create(config);
    }

}
