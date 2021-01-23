package com.cheney.redis.factory;

import com.cheney.redis.lock.awaken.listener.SubLockManager;
import com.cheney.redis.lock.executor.RedisExecutor;

/**
 * redis lock依赖类工厂接口
 *
 * @author cheney
 * @date 2019/6/6
 */
public interface RedisLockFactory {

    /**
     * 默认为spring工厂
     */
    RedisLockFactory DEFAULT_LOCK_FACTORY = new SpringRedisLockFactory();

    SubLockManager getSubLockManager();

    RedisExecutor getRedisExecutor();
}