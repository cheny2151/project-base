package com.cheney.redis.factory;

import com.cheney.exception.BusinessRunTimeException;
import com.cheney.redis.lock.awaken.listener.SpringSubLockManager;
import com.cheney.redis.lock.awaken.listener.SubLockManager;
import com.cheney.redis.lock.executor.RedisExecutor;
import com.cheney.redis.lock.executor.SpringRedisExecutor;
import com.cheney.utils.SpringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collection;

/**
 * spring整合redis锁的工厂
 *
 * @author cheney
 * @date 2020-08-17
 */
public class SpringRedisLockFactory extends CacheLockFactory {

    @Override
    protected SubLockManager newSubLockManager() {
        SubLockManager subLockManager = null;
        try {
            subLockManager = SpringUtils.getBean("springSubLockManager", SubLockManager.class);
        } catch (Exception e) {
            // try next
        }
        if (subLockManager == null) {
            Collection<SubLockManager> lockManagers = SpringUtils.getBeansOfType(SubLockManager.class);
            subLockManager = lockManagers.stream()
                    .filter(instance -> SpringSubLockManager.class.isAssignableFrom(instance.getClass()))
                    .findFirst()
                    .orElseThrow(() -> new BusinessRunTimeException("can not find any bean of SubLockManager"));
        }
        return subLockManager;
    }

    @Override
    protected RedisExecutor newRedisExecutor() {
        RedisTemplate redisTemplate = null;
        try {
            redisTemplate = SpringUtils.getBean("redisTemplate", RedisTemplate.class);
        } catch (Exception e) {
            // try next
        }
        if (redisTemplate == null) {
            Collection<RedisTemplate> redisTemplates = SpringUtils.getBeansOfType(RedisTemplate.class);
            redisTemplate = redisTemplates.stream().findFirst()
                    .orElseThrow(() -> new BusinessRunTimeException("can not find any bean of RedisTemplate"));
        }
        return new SpringRedisExecutor(redisTemplate);
    }
}