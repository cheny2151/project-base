package com.cheney.redis.client;

import com.alibaba.fastjson.JSON;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;

/**
 * String序列化的redis存储方式
 */
@Component("strRedisClient")
public class StrRedisClient<V> extends AbstractRedisClient<String> implements GenericsRedisClient<V> {

    @Resource(name = "strRedisTemplate")
    private RedisTemplate<String, String> redis;

    @Override
    @Resource(name = "strRedisTemplate")
    protected void setRedis(RedisTemplate<String, String> redis) {
        super.setRedis(redis);
    }

    private HashOperations<String, String, Object> opsForObject;

    protected HashOperations<String, String, Object> getHashOperationForObject() {
        return opsForObject == null ? (opsForObject = redis.opsForHash()) : opsForObject;
    }

    @Override
    public void HMSetForObject(String k, V kv, int days) {
        getHashOperationForObject().putAll(k, JSON.parseObject(JSON.toJSONString(kv)));
        expire(k, days);
    }

    @Override
    public void HMSetForObject(String k, V kv) {
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
    public V HMGetForObject(String k, Class<V> clazz) {
        if (!exists(k)) {
            return null;
        }
        Map<String, Object> map = getHashOperationForObject().entries(k);
        return JSON.parseObject(JSON.toJSONString(map), clazz);
    }

}
