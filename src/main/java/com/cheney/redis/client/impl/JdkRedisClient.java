package com.cheney.redis.client.impl;

import com.cheney.redis.client.AbstractMapRedisClient;
import com.cheney.redis.client.MapRedisApi;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("jdkRedisClient")
public class JdkRedisClient<V> extends AbstractMapRedisClient<V> implements MapRedisApi<V> {

    @Resource(name = "jdkRedisTemplate")
    private RedisTemplate<String,V> redis;

    @Override
    @Resource(name = "jdkRedisTemplate")
    protected void setRedis(RedisTemplate<String, V> redis) {
        super.setRedis(redis);
    }

}
