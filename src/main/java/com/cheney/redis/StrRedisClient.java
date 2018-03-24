package com.cheney.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * String序列化的redis存储方式
 */
@Component("strRedisClient")
public class StrRedisClient<V> extends AbstractRedisClient<V> {

    @Resource(name = "stringRedisSerializerTemplate")
    private RedisTemplate<String, V> redisTemplate;


    @Override
    @Resource(name = "stringRedisSerializerTemplate")
    protected void setRedisTemplate(RedisTemplate<String, V> redisTemplate) {
        super.setRedisTemplate(redisTemplate);
    }

}
