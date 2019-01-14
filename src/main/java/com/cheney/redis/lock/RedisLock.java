package com.cheney.redis.lock;

import java.util.concurrent.TimeUnit;

/**
 * @author cheney
 */
public interface RedisLock {

    boolean tryLock(long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException;

    void unLock();

}
