package com.cheney.redis.lock;

import com.cheney.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;

import java.util.List;
import java.util.UUID;

/**
 * @author cheney
 */
@Slf4j
public abstract class RedisLockAdaptor implements RedisLock, AutoCloseable {

    protected final String path;

    protected final RedisTemplate redisTemplate;

    private static final String SERVER_ID = UUID.randomUUID().toString();

    public RedisLockAdaptor(String path) {
        this.path = path;
        this.redisTemplate = SpringUtils.getBean("redisTemplate", RedisTemplate.class);
    }


    protected Object execute(String script, List<String> keys, List<String> args) {
        return redisTemplate.execute((RedisCallback<Object>) (redisConnection) -> {
            Object nativeConnection = redisConnection.getNativeConnection();

            Object result = null;

            // 集群模式
            if (nativeConnection instanceof JedisCluster) {
                result = ((JedisCluster) nativeConnection).eval(script, keys, args);
            }
            // 单机模式
            else if (nativeConnection instanceof Jedis) {
                result = ((Jedis) nativeConnection).eval(script, keys, args);
            }

            return result;
        });
    }

    protected String getCurrentThreadID() {
        return "THREAD_ID:" + (SERVER_ID + "-" + Thread.currentThread().getId()).hashCode();
    }

    public String getPath() {
        return path;
    }

    public String getServerId() {
        return SERVER_ID;
    }

    @Override
    public void close() throws Exception {
        this.unLock();
    }
}
