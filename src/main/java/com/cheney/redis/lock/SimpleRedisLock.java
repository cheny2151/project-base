package com.cheney.redis.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * todo 未实现
 *
 * @author cheney
 */
@Slf4j
public class SimpleRedisLock extends RedisLockAdaptor {

    private long leaseTimeTemp;

    /**
     * 轮询间隔时间
     */
    private final long POLLING_INTERVAL = 10;

    private final String LOCK_LUA_SCRIPT = "if (redis.call('exists', KEYS[1]) == 0) then " +
            "redis.call('hset', KEYS[1], ARGV[2], 1); " +
            "redis.call('pexpire', KEYS[1], ARGV[1]); " +
            "return nil; " +
            "end; " +
            "if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then " +
            "redis.call('hincrby', KEYS[1], ARGV[2], 1); " +
            "redis.call('pexpire', KEYS[1], ARGV[1]); " +
            "return nil; " +
            "end; " +
            "return redis.call('pttl', KEYS[1]);";

    private final String UNLOCK_LUA_SCRIPT = "if (redis.call('exists', KEYS[1]) == 0) then " +
            "return 1; " +
            "end;" +
            "if (redis.call('hexists', KEYS[1], ARGV[2]) == 0) then " +
            "return nil;" +
            "end; " +
            "local counter = redis.call('hincrby', KEYS[1], ARGV[2], -1); " +
            "if (counter > 0) then " +
            "redis.call('pexpire', KEYS[1], ARGV[1]); " +
            "return 0; " +
            "else " +
            "redis.call('del', KEYS[1]); " +
            "return 1; " +
            "end; " +
            "return nil;";

    public SimpleRedisLock(String path) {
        super(path);
    }

    public boolean tryLock(long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException {
        waitTime = timeUnit.toMillis(waitTime);
        leaseTime = timeUnit.toMillis(leaseTime);
        long beginTime = System.currentTimeMillis();
        //执行加锁脚本,返回null则获取锁成功
        Object result = LockScript(leaseTime, timeUnit);
        this.leaseTimeTemp = leaseTime;

        //当加锁脚本返回不为null并且还需要继续等待获取锁时执行while语句(轮询获取锁)
        while (result != null
                && (waitTime == -1
                || beginTime + waitTime > System.currentTimeMillis())) {
            log.info("未能抢到锁，轮询ing...");
            Thread.sleep(POLLING_INTERVAL);
            result = LockScript(leaseTime, timeUnit);
        }

        return result == null;
    }

    private Object LockScript(long leaseTime, TimeUnit timeUnit) {
        List<String> keys = Collections.singletonList(path);
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(timeUnit.toMillis(leaseTime)));
        args.add(getCurrentThreadID());
        return execute(LOCK_LUA_SCRIPT, keys, args);
    }

    public void unLock() {
        Object result = unLockScript();
        if (result == null) {
            log.info("解锁失败:redis未上该锁");
        } else if (1 == (long) result) {
            log.info("解锁成功");
        } else if (0 == (long) result) {
            log.info("减少重入次数，并且刷新了锁定时间");
        }
    }

    private Object unLockScript() {
        List<String> keys = Collections.singletonList(path);
        ArrayList<String> args = new ArrayList<>();
        args.add(String.valueOf(leaseTimeTemp));
        args.add(getCurrentThreadID());
        return execute(UNLOCK_LUA_SCRIPT, keys, args);
    }

}
