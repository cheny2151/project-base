package com.cheney.redis.client.impl;

import com.alibaba.fastjson.JSON;
import com.cheney.redis.client.AbstractMapRedisClient;
import com.cheney.redis.client.MapRedisApi;
import com.cheney.redis.client.ObjectRedisApi;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

@Component("jdkRedisClient")
public class JdkRedisClient<V> extends AbstractMapRedisClient<V> implements MapRedisApi<V>, ObjectRedisApi {

    @Resource(name = "jdkRedisTemplate")
    private RedisTemplate<String,V> redis;

    @Override
    @Resource(name = "jdkRedisTemplate")
    protected void setRedis(RedisTemplate<String, V> redis) {
        super.setRedis(redis);
    }

    private HashOperations<String, String, Object> opsForObject;

    protected HashOperations<String, String, Object> getHashOperationForObject() {
        return opsForObject == null ? (opsForObject = redis.opsForHash()) : opsForObject;
    }

    @Override
    public <HV> void HMSetObject(String k, HV kv, int days) {
        getHashOperationForObject().putAll(k, JSON.parseObject(JSON.toJSONString(kv)));
        expire(k, days);
    }

    @Override
    public <HV> void HMSetObject(String k, HV kv) {
        getHashOperationForObject().putAll(k, JSON.parseObject(JSON.toJSONString(kv)));
    }

    @Override
    public void HSetField(String k, String hk, Object v) {
        getHashOperationForObject().put(k, hk, v);
    }

    @Override
    public Object HGetField(String k, String hk) {
        return getHashOperationForObject().get(k, hk);
    }

    @Override
    public <HV> HV HMGetObject(String k, Class<HV> clazz) {
        if (!exists(k)) {
            return null;
        }
        Map<String, Object> map = getHashOperationForObject().entries(k);
        return JSON.parseObject(JSON.toJSONString(map), clazz);
    }

}
