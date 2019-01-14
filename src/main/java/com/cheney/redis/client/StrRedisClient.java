package com.cheney.redis.client;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * String序列化的redis存储方式
 */
@Component("strRedisClient")
public class StrRedisClient extends AbstractRedisClient<String> {

    @Resource(name = "strRedisTemplate")
    private RedisTemplate<String, String> redis;

    @Override
    @Resource(name = "strRedisTemplate")
    protected void setRedis(RedisTemplate<String, String> redis) {
        super.setRedis(redis);
    }

}
