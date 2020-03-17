package com.cheney.redis.lock.awaken;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static com.cheney.redis.lock.LockConstant.*;
import static com.cheney.redis.lock.awaken.listener.SubLockManager.AWAKE_MESSAGE;

/**
 * redis重入锁
 * 利用redis的发布订阅，在加锁失败时订阅其他线程的redis解锁信息，然后阻塞线程，
 * 等到其他线程解锁时唤醒线程再循环获取该锁，直至获取到锁或者超时时退出
 * <p>
 * 注意：此锁的lua脚本出现操作多个key，必须有{}
 *
 * @author cheney
 */
@Slf4j
public class MultiPathRedisLock extends AwakenRedisLock {

    /**
     * 多路径path
     */
    private Set<String> multiPaths;

    public MultiPathRedisLock(String path, Collection<String> multiPaths) {
        super(path);
        if (CollectionUtils.isEmpty(multiPaths)) {
            throw new IllegalArgumentException("multi paths can not be empty");
        }
        this.multiPaths = new HashSet<>(multiPaths);
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
        keys.add(getSetKey());
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(leaseTime));
        args.addAll(multiPaths);
        return execute(MULTI_LOCK_LUA_SCRIPT, keys, args);
    }

    /**
     * 执行解锁脚本
     *
     * @return redis执行脚本返回值
     */
    protected Object unLockScript() {
        List<String> keys = new ArrayList<>();
        keys.add(path);
        keys.add(getSetKey());
        keys.add(getChannelName());
        List<String> args = new ArrayList<>();
        args.add(AWAKE_MESSAGE);
        args.addAll(multiPaths);
        return execute(MULTI_UNLOCK_LUA_SCRIPT, keys, args);
    }

    protected String getChannelName() {
        return LOCK_CHANNEL + path;
    }

    @Override
    public String pathPreLabel() {
        return "MULTI:";
    }

    private String getSetKey() {
        return "SET:" + path;
    }
}
