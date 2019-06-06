package com.cheney.redis.lock;

import java.util.concurrent.TimeUnit;

/**
 * @author cheney
 */
public interface RedisLock extends AutoCloseable {

    /**
     * 锁资源的标识path
     */
    String getPath();

    /**
     * 尝试获取redis同步锁
     *
     * @param waitTime  最长等待时间
     * @param leaseTime 超时自动释放锁时间
     * @param timeUnit  时间单位
     * @return 是否成功获取锁
     */
    boolean tryLock(long waitTime, long leaseTime, TimeUnit timeUnit);

    /**
     * 尝试释放锁
     */
    void unLock();

}
