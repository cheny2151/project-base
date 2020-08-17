package com.cheney.redis.factory;

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
public class SpringRedisLockFactory implements RedisLockFactory {

    @Override
    public SubLockManager getSubLockManager() {
        return SpringUtils.getBean("springSubLockManager", SubLockManager.class);
    }

    @Override
    public RedisExecutor getRedisExecutor() {
        RedisTemplate redisTemplate = null;
        try {
            redisTemplate = SpringUtils.getBean("redisTemplate", RedisTemplate.class);
        } catch (Exception e) {
            // try next
        }
        if (redisTemplate == null) {
            Collection<RedisTemplate> redisTemplates = SpringUtils.getBeansOfType(RedisTemplate.class);
            if (redisTemplates != null && redisTemplates.size() > 0) {
                redisTemplate = redisTemplates.stream().findFirst().get();
            }
        }
        return new SpringRedisExecutor(redisTemplate);
    }
}
