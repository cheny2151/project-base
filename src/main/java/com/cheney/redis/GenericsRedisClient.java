package com.cheney.redis;

/**
 * 用于泛型继承树
 *
 * @param <V>
 */
public interface GenericsRedisClient<V> extends RedisClient<V> {

    //------------------------------ hash for object------------------------------

    void HMSetForObject(String k, V kv, int days);

    void HMSetForObject(String k, V kv);

    void HSetForObject(String k, String hk, Object v);

    Object HGetForObject(String k, String hk);

    V HMGetForObject(String k,Class<V> clazz);

}
