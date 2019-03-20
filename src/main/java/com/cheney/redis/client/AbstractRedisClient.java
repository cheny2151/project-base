package com.cheney.redis.client;

import com.cheney.exception.RedisException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * redis客户端基础方法抽象类(实现类需要注入redisTemplate的实现类)
 *
 * @param <V>
 */
public abstract class AbstractRedisClient<V> implements RedisClient<V> {

    protected RedisTemplate<String, V> redis;

    protected void setRedis(RedisTemplate<String, V> redis) {
        this.redis = redis;
    }

    private HashOperations<String, String, V> opsForMap;

    /**
     * 由于每次执行opsForHash()方法都会new一个，提取。
     */
    protected HashOperations<String, String, V> getHashOperationForMap() {
        return opsForMap == null ? (opsForMap = redis.opsForHash()) : opsForMap;
    }

    //------------------------------ common ------------------------------

    @Override
    public void expire(String k, int days) {
        redis.expire(k, days, TimeUnit.DAYS);
    }

    @Override
    public void expire(String k, long timeout, TimeUnit timeUnit) {
        redis.expire(k, timeout, timeUnit);
    }

    @Override
    public void removeKey(String k) {
        redis.delete(k);
    }

    @Override
    public boolean exists(String k) {
        Boolean exists = redis.hasKey(k);
        if (exists == null) {
            throw new RedisException("exists key:" + k + " return null");
        }
        return exists;
    }

    @Override
    public void delete(String k) {
        redis.delete(k);
    }

    @Override
    public long getExpire(String k, TimeUnit timeUnit) {
        Long expire = redis.getExpire(k, timeUnit);
        if (expire == null){
            throw new RedisException("expire key:" + k + " return null");
        }
        return expire;
    }

    //------------------------------ value ------------------------------

    @Override
    public void setValue(String k, V v, int days) {
        setValue(k, v);
        expire(k, days);
    }

    @Override
    public void setValue(String k, V v) {
        redis.opsForValue().set(k, v);
    }

    @Override
    public V getValue(String k) {
        return redis.opsForValue().get(k);
    }

    //------------------------------ list ------------------------------

    @Override
    public void addList(String k, List<V> values) {
        rightPushList(k, values);
    }

    @Override
    public void addList(String k, V v) {
        rightPush(k, v);
    }

    @Override
    public void rightPushList(String k, List<V> values) {
        redis.opsForList().rightPushAll(k, values);
    }

    @Override
    public void rightPush(String k, V v) {
        redis.opsForList().rightPush(k, v);
    }

    @Override
    public void leftPushList(String k, List<V> values) {
        redis.opsForList().leftPushAll(k, values);
    }

    @Override
    public void leftPush(String k, V v) {
        redis.opsForList().leftPush(k, v);
    }

    @Override
    public List<V> getList(String k) {
        List<V> range;
        return (range = redis.opsForList().range(k, 0, listSize(k) - 1)) == null || range.size() == 0 ? null : range;
    }

    @Override
    public V rightPop(String k) {
        return redis.opsForList().rightPop(k);
    }

    @Override
    public V leftPop(String k) {
        return redis.opsForList().leftPop(k);
    }

    @Override
    public Long listSize(String k) {
        return redis.opsForList().size(k);
    }

    //------------------------------ hash ------------------------------

    @Override
    public boolean HHasKey(String k, String hk) {
        return getHashOperationForMap().hasKey(k, hk);
    }

    @Override
    public Set<String> HKeys(String k) {
        Set<String> keys;
        return (keys = getHashOperationForMap().keys(k)) == null || keys.size() == 0 ? null : keys;
    }

    @Override
    public long HDel(String k, String hk) {
        return getHashOperationForMap().delete(k, hk);
    }

}
