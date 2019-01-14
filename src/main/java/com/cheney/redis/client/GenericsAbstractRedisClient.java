package com.cheney.redis.client;

import com.cheney.utils.JsonUtils;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Map;

/**
 * redis客户端泛型抽象类(实现类需要注入redisTemplate的实现类)
 * 实现了用hash保存对象的方式
 *
 * @param <V>
 */
public abstract class GenericsAbstractRedisClient<V> extends AbstractRedisClient<V> implements GenericsRedisClient<V> {

    private HashOperations<String, String, Object> opsForObject;

    protected HashOperations<String, String, Object> getHashOperationForObject() {
        return opsForObject == null ? (opsForObject = redis.opsForHash()) : opsForObject;
    }

    @Override
    protected void setRedis(RedisTemplate<String, V> redis) {
        super.setRedis(redis);
    }

    @Override
    public void HMSetForObject(String k, V kv, int days) {
        getHashOperationForObject().putAll(k, JsonUtils.object2Map(kv));
        expire(k, days);
    }

    @Override
    public void HMSetForObject(String k, V kv) {
        getHashOperationForObject().putAll(k, JsonUtils.object2Map(kv));
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
        Map<String, Object> map = getHashOperationForObject().entries(k);
        return JsonUtils.map2Object(map, clazz);
    }


}
