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
 * 注意：默认情况下，redis用整个key做哈希，在redis集群环境下，根据不同的key哈希定位到不同的slot，进而确定是哪个节点。
 * 加了{ } 就只会用花括号里面的字符串做哈希，所以同样{}的key一定可以在同一个slot，而lua脚本里有多个key的情况下，为了保证
 * 原子性操作，如果有两个或以上key落在不同的slot则会报错--> No way to dispatch this command to Redis Cluster because keys have different slots.
 * 所以为了保证每个key都落在同一个slot上，可以在key上引入相同的{ }字符串
 */
@Slf4j
public abstract class RedisLockAdaptor implements RedisLock {

    protected final String path;

    protected boolean isLock;

    protected final RedisTemplate redisTemplate;

    private static final String SERVER_ID = UUID.randomUUID().toString();

    public RedisLockAdaptor(String path) {
        this.path = "{" + path + "}";
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

    protected abstract Object LockScript(long leaseTime);

    protected abstract Object unLockScript();

    public void unLock() {
        if (!isLock) {
            //为获取锁，不执行解锁脚本
            return;
        }
        Object result = unLockScript();
        if (result == null) {
            isLock = false;
            log.info("unlock fail:redis未上该锁");
        } else if (1 == (long) result) {
            isLock = false;
            log.info("unlock success");
        } else if (0 == (long) result) {
            log.info("count down:减少重入次数，并且刷新了锁定时间");
        }
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
