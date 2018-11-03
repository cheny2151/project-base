package com.cheney.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("jdkRedisClient")
public class JdkRedisClient<V> extends AbstractRedisClient<V> {

    @Resource(name = "jdkRedisTemplate")
    private RedisTemplate<String,V> redis;

    @Override
    @Resource(name = "jdkRedisTemplate")
    protected void setRedis(RedisTemplate<String, V> redis) {
        super.setRedis(redis);
    }

}
