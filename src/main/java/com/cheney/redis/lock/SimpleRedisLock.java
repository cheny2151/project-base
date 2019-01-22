package com.cheney.redis.lock;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.cheney.redis.lock.LockConstant.LOCK_LUA_SCRIPT;
import static com.cheney.redis.lock.LockConstant.UNLOCK_LUA_SCRIPT;

/**
 * 获取重入锁成功则返回，失败则轮询直到获取到锁或者waitTime时间结束
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

    public SimpleRedisLock(String path) {
        super(path);
    }

    public boolean tryLock(long waitTime, long leaseTime, TimeUnit timeUnit) {
        waitTime = timeUnit.toMillis(waitTime);
        leaseTime = timeUnit.toMillis(leaseTime);
        long beginTime = System.currentTimeMillis();
        //执行加锁脚本,返回null则获取锁成功
        Object result = LockScript(leaseTime, timeUnit);
        this.leaseTimeTemp = leaseTime;

        try {
            //当加锁脚本返回不为null并且还需要继续等待获取锁时执行while语句(轮询获取锁)
            while (result != null
                    && (waitTime == -1
                    || beginTime + waitTime > System.currentTimeMillis())) {
                log.debug("未能抢到锁，轮询ing...");
                Thread.sleep(POLLING_INTERVAL);
                result = LockScript(leaseTime, timeUnit);
            }
        } catch (Exception e) {
            log.error("try lock error", e);
            return false;
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
            log.info("unLock success");
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
