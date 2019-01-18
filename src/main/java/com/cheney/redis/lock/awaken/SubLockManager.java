package com.cheney.redis.lock.awaken;

import com.cheney.redis.lock.LockConstant;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPubSub;

import javax.annotation.PostConstruct;

/**
 * @author cheney
 */
@Component
public class SubLockManager extends JedisPubSub {

    @PostConstruct
    public void initSub() {
        this.psubscribe(LockConstant.LOCK_CHANNEL + "*");
    }

    @Override
    public void onPMessage(String pattern, String channel, String message) {
        System.out.println(channel);
        System.out.println(message);
    }

}
