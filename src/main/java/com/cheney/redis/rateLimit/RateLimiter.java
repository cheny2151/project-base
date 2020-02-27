package com.cheney.redis.rateLimit;

import com.cheney.redis.RedisEval;
import com.cheney.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

import static com.cheney.redis.rateLimit.RateLimitScript.KEY_PRE_CHANNEL;

/**
 * @author cheney
 * @date 2020-02-27
 */
@Slf4j
public class RateLimiter implements RedisEval {

    /**
     * 限流路径
     */
    private String path;

    /**
     * 最大令牌数
     */
    private int maxPermits;

    /**
     * qps
     */
    private int rate;

    private RedisTemplate<?, ?> redisTemplate;

    public RateLimiter(String path, int maxPermits, int rate) {
        if (StringUtils.isEmpty(path)) {
            throw new IllegalArgumentException();
        }
        this.path = path;
        this.maxPermits = maxPermits;
        this.rate = rate;
        this.init();
    }

    private void init() {
        this.redisTemplate = SpringUtils.getBean("redisTemplate", RedisTemplate.class);
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(maxPermits));
        args.add(String.valueOf(rate));
        args.add(String.valueOf(maxPermits));
        execute(redisTemplate, RateLimitScript.INIT,
                Collections.singletonList(path), args);
    }

    public boolean acquire() {
        return acquire(1);
    }

    public boolean acquire(int permits) {
        if (permits <= 0) {
            return true;
        }
        Object result = execute(redisTemplate, RateLimitScript.INIT,
                Collections.singletonList(path),
                Collections.singletonList(String.valueOf(permits)));
        return result != null;
    }

    public boolean tryAcquire(long waitTime, TimeUnit timeUnit) {
        return tryAcquire(1, waitTime, timeUnit);
    }

    public boolean tryAcquire(int permits, long waitTime, TimeUnit timeUnit) {
        long maxTime = System.currentTimeMillis() + timeUnit.toMillis(waitTime);
        long nanos = TimeUnit.SECONDS.toNanos(1);

        boolean result;
        try {
            while (!(result = acquire(permits))) {
                long timeout = maxTime - System.currentTimeMillis();
                if (timeout <= 0) {
                    //timeout return false
                    break;
                }
                LockSupport.parkNanos(this, nanos);
            }
        } catch (Exception e) {
            log.error("try lock error", e);
            return false;
        }

        return result;
    }

    public String getPath() {
        return path;
    }

    public Integer getMaxPermits() {
        return maxPermits;
    }

    public Integer getRate() {
        return rate;
    }

    private String getChannelName() {
        return KEY_PRE_CHANNEL + path;
    }
}
