package com.cheney.redis;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public interface RedisClient<V> {

    //------------------------------ common ------------------------------

    void expire(String k, int days);

    void delete(String k);

    boolean containsKey(String k);

    void removeKey(String k);

    boolean exists(String k);

    long getExpire(String k, TimeUnit timeUnit);

    //------------------------------ value ------------------------------

    void setValue(String k, V v, int days);

    void setValue(String k, V v);

    V getValue(String k);

    //------------------------------ list ------------------------------

    void addList(String k, List<V> values);

    void addList(String k, V v);

    void rightPushList(String k, List<V> values);

    void rightPush(String k, V v);

    void leftPushList(String k, List<V> values);

    void leftPush(String k, V v);

    List<V> getList(String k);

    V rightPop(String k);

    V leftPop(String k);

    Long listSize(String k);

    //------------------------------ hash ------------------------------

    void HMSet(String k, Map<String, V> kv, int days);

    void HMSet(String k, Map<String, V> kv);

    void HSet(String k, String hk, V v);

    V HGet(String k, String hk);

    boolean HHasKey(String k, String hk);

    Map<String, V> HMGet(String k);

    Set<String> HKeys(String k);

    List<V> HValues(String k);

    long HDel(String k, String hk);


}
