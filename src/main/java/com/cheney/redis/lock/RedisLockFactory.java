package com.cheney.redis.lock;

import com.cheney.redis.lock.awaken.listener.JedisSubLockManager;
import com.cheney.redis.lock.awaken.listener.SubLockManager;
import com.cheney.utils.SpringUtils;

/**
 * redis lock类工厂
 *
 * @author cheney
 * @date 2019/6/6
 */
public class RedisLockFactory {

//    private static SubLockManager subLockManager = new JedisSubLockManager();

//    public static SubLockManager getSubLockManager() {
//        return RedisLockFactory.subLockManager;
//    }

//    public static void setSubLockManager(SubLockManager springSubLockManager) {
//        RedisLockFactory.subLockManager = springSubLockManager;
//    }

    public static SubLockManager getSpringSubLockManager() {
        return SpringUtils.getBean("springSubLockManager", SubLockManager.class);
    }
}
