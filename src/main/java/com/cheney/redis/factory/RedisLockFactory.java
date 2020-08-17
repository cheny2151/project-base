package com.cheney.redis.factory;

import com.cheney.redis.lock.awaken.listener.SubLockManager;
import com.cheney.redis.lock.executor.RedisExecutor;

/**
 * redis lock类工厂
 *
 * @author cheney
 * @date 2019/6/6
 */
public interface RedisLockFactory {

    RedisLockFactory DEFAULT_LOCK_FACTORY = new SpringRedisLockFactory();

    SubLockManager getSubLockManager();

    RedisExecutor getRedisExecutor();
}
