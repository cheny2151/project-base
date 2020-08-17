package com.cheney.redis.lock.executor;

import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.api.async.RedisScriptingAsyncCommands;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.Collections;
import java.util.List;

/**
 * 整合spring执行lua脚本
 *
 * @author cheney
 * @date 2020-08-17
 */
public class SpringRedisExecutor implements RedisExecutor {

    private RedisTemplate redisTemplate;

    public SpringRedisExecutor(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public Object execute(String script, List<String> keys, List<String> args) {
        return execute(redisTemplate, script, keys, args);
    }

    private Object execute(RedisTemplate redisTemplate, String script, List<String> keys, List<String> args) {
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
            } else if (nativeConnection instanceof RedisScriptingAsyncCommands) {
                try {
                    @SuppressWarnings("unchecked")
                    RedisScriptingAsyncCommands<Object, Object> commands = (RedisScriptingAsyncCommands<Object, Object>) nativeConnection;
                    result = commands
                            .eval(script, ScriptOutputType.INTEGER,
                                    toBytes(finalKeys),
                                    toBytes(finalArgs))
                            .get();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException();
                }

            }

            return result;
        });
    }

    private Object[] toBytes(List<String> list) {
        return list.stream().map(String::getBytes).toArray();
    }
}
