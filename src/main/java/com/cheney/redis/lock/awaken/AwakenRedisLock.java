package com.cheney.redis.lock.awaken;

import com.cheney.redis.lock.RedisLockAdaptor;
import com.cheney.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.cheney.redis.lock.LockConstant.*;

/**
 * redis重入锁
 * 利用redis的发布订阅，在加锁失败时订阅其他线程的redis解锁信息，然后阻塞线程，
 * 等到其他线程解锁时唤醒线程再循环获取该锁，直至获取到锁或者超时时退出
 *
 * 注意：此锁的lua脚本出现操作多个key，必须有{}
 *
 * @author cheney
 */
@Slf4j
public class AwakenRedisLock extends RedisLockAdaptor {

    private long leaseTimeTemp;

    private SubLockManager subLockManager;

    public AwakenRedisLock(String path) {
        super(path);
        subLockManager = SpringUtils.getBean("subLockManager", SubLockManager.class);
    }

    public boolean tryLock(long waitTime, long leaseTime, TimeUnit timeUnit) {

        long maxTime = System.currentTimeMillis() + timeUnit.toMillis(waitTime);
        leaseTimeTemp = leaseTime = timeUnit.toMillis(leaseTime);

        //try lock
        Object result;
        try {
            while ((result = LockScript(leaseTime)) != null) {
                long timeout = maxTime - System.currentTimeMillis();
                if (timeout <= 0) {
                    //timeout return false
                    break;
                }
                CountDownLatch countDownLatch = new CountDownLatch(1);
                //add listener
                subLockManager.addMessageListener(
                        new LockListener(getChannelName(), countDownLatch::countDown)
                );
                countDownLatch.await(timeout, timeUnit);
            }
        } catch (Exception e) {
            log.error("try lock error", e);
            return false;
        }

        return isLock = (result == null);
    }

    protected Object LockScript(long leaseTime) {
        List<String> keys = Collections.singletonList(path);
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(leaseTime));
        args.add(getCurrentThreadID());
        return execute(AWAKEN_LOCK_LUA_SCRIPT, keys, args);
    }

    protected Object unLockScript() {
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