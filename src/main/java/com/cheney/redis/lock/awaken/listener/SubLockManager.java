package com.cheney.redis.lock.awaken.listener;

/**
 * @author cheney
 * @date 2019/6/6
 */
public interface SubLockManager {

    void addMessageListener(LockListener lockListener);

}
