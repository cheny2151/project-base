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
public class AwakenRedisLock extends RedisLockAdaptor {

    private final String UNLOCK_MESSAGE = "UN_LOCK";

    private long leaseTimeTemp;

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
            "redis.call('publish', KEYS[2], ARGV[1]); " +
            "return 1; " +
            "end;" +
            "if (redis.call('hexists', KEYS[1], ARGV[3]) == 0) then " +
            "return nil;" +
            "end; " +
            "local counter = redis.call('hincrby', KEYS[1], ARGV[3], -1); " +
            "if (counter > 0) then " +
            "redis.call('pexpire', KEYS[1], ARGV[2]); " +
            "return 0; " +
            "else " +
            "redis.call('del', KEYS[1]); " +
            "redis.call('publish', KEYS[2], ARGV[1]); " +
            "return 1; " +
            "end; " +
            "return nil;";

    public AwakenRedisLock(String path) {
        super(path);
    }

    public boolean tryLock(long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException {

        leaseTimeTemp = leaseTime = timeUnit.toMillis(leaseTime);

        return false;
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
        ArrayList<String> keys = new ArrayList<>();
        keys.add(path);
        keys.add(getChannelName());
        ArrayList<String> args = new ArrayList<>();
        args.add(UNLOCK_MESSAGE);
        args.add(String.valueOf(leaseTimeTemp));
        args.add(getCurrentThreadID());
        return execute(UNLOCK_LUA_SCRIPT, keys, args);
    }

    private String getChannelName() {
        return "LOCK_CHANNEL:" + path;
    }


}
