package com.cheney.redis.lock.awaken;

import com.cheney.redis.lock.RedisLockAdaptor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.cheney.redis.lock.LockConstant.*;

/**
 * todo 未实现
 *
 * @author cheney
 */
@Slf4j
public class AwakenRedisLock extends RedisLockAdaptor {

    private long leaseTimeTemp;

    public AwakenRedisLock(String path) {
        super(path);
    }

    public boolean tryLock(long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException {

        waitTime = timeUnit.toMillis(waitTime);
        leaseTimeTemp = leaseTime = timeUnit.toMillis(leaseTime);

        Object result = LockScript(leaseTime);
        //获取锁成功
        if (result == null) {
            return true;
        }

        return false;
    }

    private Object LockScript(long leaseTime) {
        List<String> keys = Collections.singletonList(path);
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(leaseTime));
        args.add(getCurrentThreadID());
        return execute(AWAKEN_LOCK_LUA_SCRIPT, keys, args);
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
        return execute(AWAKEN_UNLOCK_LUA_SCRIPT, keys, args);
    }

    private String getChannelName() {
        return LOCK_CHANNEL + path;
    }


}
