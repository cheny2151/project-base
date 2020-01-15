package com.cheney.redis.lock.awaken;

import com.cheney.redis.lock.RedisLockAdaptor;
import com.cheney.redis.lock.RedisLockFactory;
import com.cheney.redis.lock.awaken.listener.LockListener;
import com.cheney.redis.lock.awaken.listener.SubLockManager;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static com.cheney.redis.lock.LockConstant.*;

/**
 * 二级锁
 *
 * @author cheney
 * @date 2020-01-13
 */
@Slf4j
public class SecondLevelRedisLock extends RedisLockAdaptor {

    /**
     * 锁类型标志
     */
    private final static String LOCK_TYPE_FLAG = "LOCK_TYPE";

    /**
     * 一级锁类型
     */
    public final static short TYPE_FIRST_LEVEL = 0;

    /**
     * 二级锁类型
     */
    public final static short TYPE_SECOND_LEVEL = 1;

    /**
     * 订阅锁状态manger
     */
    private SubLockManager subLockManager;

    /**
     * master锁标识
     */
    private String secondPath;

    /**
     * 0：一级锁，1：二级锁
     */
    private short type;

    private SecondLevelRedisLock(String firstPath, String secondPath) {
        super(firstPath);
        this.secondPath = secondPath;
        type = secondPath == null ? TYPE_FIRST_LEVEL : TYPE_SECOND_LEVEL;
        subLockManager = RedisLockFactory.getSpringSubLockManager();
    }

    public static SecondLevelRedisLock firstLevelLock(String firstPath) {
        return new SecondLevelRedisLock(firstPath, null);
    }

    public static SecondLevelRedisLock secondLevelLock(String firstPath, String secondPath) {
        return new SecondLevelRedisLock(firstPath, secondPath);
    }

    public boolean tryLock(long waitTime, long leaseTime, TimeUnit timeUnit) {

        long maxTime = System.currentTimeMillis() + timeUnit.toMillis(waitTime);
        leaseTime = timeUnit.toMillis(leaseTime);

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

        boolean isLock = result == null;
        this.isLock.set(isLock);

        if (!isLock) {
            log.info("Redis try lock fail,lock path:{}", getPath());
        }

        return isLock;
    }

    /**
     * 执行上锁脚本
     *
     * @param leaseTime 超时释放锁时间
     * @return redis执行脚本返回值
     */
    protected Object LockScript(long leaseTime) {
        List<String> keys = new ArrayList<>();
        keys.add(path);
        keys.add(LOCK_TYPE_FLAG);
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(leaseTime));
        args.add(String.valueOf(type));
        if (type == 1) {
            args.add(secondPath);
        }
        return execute(SECONDARY_LOCK_LUA_SCRIPT, keys, args);
    }

    /**
     * 执行解锁脚本
     *
     * @return redis执行脚本返回值
     */
    protected Object unLockScript() {
        ArrayList<String> keys = new ArrayList<>();
        keys.add(path);
        keys.add(LOCK_TYPE_FLAG);
        keys.add(getChannelName());
        ArrayList<String> args = new ArrayList<>();
        args.add(UNLOCK_MESSAGE);
        args.add(String.valueOf(type));
        args.add(secondPath);
        return execute(SECONDARY_UNLOCK_LUA_SCRIPT, keys, args);
    }

    private String getChannelName() {
        String base = LOCK_CHANNEL + path;
        if (TYPE_SECOND_LEVEL == type) {
            return base + ":" + secondPath;
        }
        return base;
    }

    /**
     * 获取二级锁标识
     *
     * @return 二级锁
     */
    public String getSecondPath() {
        return secondPath;
    }

    /**
     * 获取锁类型
     *
     * @return 0：一级锁，1：二级锁
     */
    public short getType() {
        return type;
    }
}
