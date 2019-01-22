package com.cheney.redis.lock.awaken;

import com.cheney.redis.lock.LockConstant;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

import java.util.LinkedList;

/**
 * 订阅redis解锁信息
 *
 * @author cheney
 */
public class SubLockManager implements MessageListener {

    public LinkedList<LockListener> LockListeners = new LinkedList<>();

    private final Object lock = new Object();

    @Override
    public void onMessage(Message message, byte[] bytes) {
        String unlockMsg = new String(message.getBody());
        String channel = new String(message.getChannel());
        if (LockConstant.UNLOCK_MESSAGE.equals(unlockMsg)) {
            synchronized (lock) {
                LockListeners.stream()
                        .filter(lockListener -> channel.equals(lockListener.getListenerChannel()))
                        .forEach(LockListener::handleListener);
                LockListeners.removeIf(lockListener -> channel.equals(lockListener.getListenerChannel()));
            }
        }
    }

    public void addMessageListener(LockListener lockListener) {
        synchronized (lock) {
            LockListeners.add(lockListener);
        }
    }

}
