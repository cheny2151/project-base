package com.cheney.redis.lock.awaken.listener;

/**
 * @author cheney
 * @date 2019/6/6
 */
public interface SubLockManager {

    /**
     * awake标识
     */
    String AWAKE_MESSAGE = "AWAKE";

    void addMessageListener(LockListener lockListener);

}
