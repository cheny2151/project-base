package com.cheney.redis.redission;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁工具
 */
//@Component("redissonLock")
@Slf4j
public class RedissonLock {

    @Resource(name = "redissonClient")
    private RedissonClient redissonClient;

    public void lock(String path) {
        RLock lock = redissonClient.getLock(path);
        lock.lock();
    }

    public boolean tryLock(String path, long waitTime, long laseTime, Runnable runnable) throws Exception {
        return tryLock(path, waitTime, laseTime, TimeUnit.SECONDS, runnable);
    }

    public boolean tryLock(String path, long waitTime, long leaseTime, TimeUnit timeUnit, Runnable runnable) throws Exception {
        RLock lock = redissonClient.getLock(path);
        boolean success = lock.tryLock(waitTime, leaseTime, timeUnit);
        try {
            if (success) {
                log.info("redis:加锁成功,path->{}", path);
                //执行任务
                runnable.run();
            } else {
                log.info("redis:获取锁失败,path->{}", path);
                return false;
            }
        } catch (Exception e) {
            log.error("redis:加锁执行任务失败,path->{}", path, e);
            throw e;
        } finally {
            //成功获取锁时释放锁
            if (success) {
                lock.unlock();
            }
        }
        return true;
    }


}
