package com.cheney.redis.factory;

import com.cheney.redis.lock.awaken.listener.SubLockManager;
import com.cheney.redis.lock.executor.RedisExecutor;

/**
 * 为RedisLockFactory提供缓存
 *
 * @author cheney
 * @date 2020-08-17
 */
public abstract class CacheLockFactory implements RedisLockFactory {

    private SubLockManager subLockManagerCache;

    private RedisExecutor redisExecutorCache;

    @Override
    public SubLockManager getSubLockManager() {
        if (subLockManagerCache == null) {
            synchronized (this) {
                if (subLockManagerCache == null) {
                    subLockManagerCache = newSubLockManager();
                }
            }
        }
        return subLockManagerCache;
    }

    @Override
    public RedisExecutor getRedisExecutor() {
        if (redisExecutorCache == null) {
            synchronized (this) {
                if (redisExecutorCache == null) {
                    redisExecutorCache = newRedisExecutor();
                }
            }
        }
        return redisExecutorCache;
    }


    protected abstract SubLockManager newSubLockManager();

    protected abstract RedisExecutor newRedisExecutor();
}
