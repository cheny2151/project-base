package com.cheney.redis.client.impl;

import com.cheney.redis.client.AbstractMapRedisClient;
import com.cheney.redis.client.MapRedisApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author cheney
 */
@Component("jsonRedisClient")
public class JsonRedisClient<V> extends AbstractMapRedisClient<V> implements MapRedisApi<V> {

    @Resource(name = "jsonRedisTemplate")
    private RedisTemplate<String, V> redisTemplate;

    @Autowired
    protected void setRedis(@Qualifier("jsonRedisTemplate") RedisTemplate<String, V> redis) {
        super.setRedis(redis);
    }


}
