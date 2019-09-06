package com.cheney.redis;

import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.Collections;
import java.util.List;

/**
 * redis脚本执行接口
 *
 * @author cheney
 * @date 2019-09-03
 */
public interface RedisEval {

    default Object execute(RedisTemplate redisTemplate, String script, List<String> keys, List<String> args) {
        final List<String> finalKeys = keys == null ? Collections.emptyList() : keys;
        final List<String> finalArgs = args == null ? Collections.emptyList() : args;
        return redisTemplate.execute((RedisCallback<Object>) (redisConnection) -> {
            Object nativeConnection = redisConnection.getNativeConnection();

            Object result = null;

            // 集群模式
            if (nativeConnection instanceof JedisCluster) {
                result = ((JedisCluster) nativeConnection).eval(script, finalKeys, finalArgs);
            }
            // 单机模式
            else if (nativeConnection instanceof Jedis) {
                result = ((Jedis) nativeConnection).eval(script, finalKeys, finalArgs);
            }

            return result;
        });
    }

}
