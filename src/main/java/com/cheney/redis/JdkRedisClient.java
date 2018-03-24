package com.cheney.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("jdkRedisClient")
public class JdkRedisClient<V> extends AbstractRedisClient<V> {

    @Resource(name = "jdkRedisSerializerTemplate")
    private RedisTemplate<String,V> redisTemplate;

    @Override
    @Resource(name = "jdkRedisSerializerTemplate")
    protected void setRedisTemplate(RedisTemplate<String, V> redisTemplate) {
        super.setRedisTemplate(redisTemplate);
    }

}
