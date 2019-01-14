package com.cheney.redis.lock;

import com.cheney.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * todo lettuce （2.0使用lettuce代替jedis，但lettuce的RedisAsyncCommands类执行eval一直报错，待解决）
 *
 * @author cheney
 */
@Slf4j
public class SimpleRedisLockForLettuce {

    private final RedisTemplate<String, String> redisTemplate;

    private final String path;

    private final String SERVER_ID = UUID.randomUUID().toString();

    private long leaseTimeTemp;

    /**
     * 轮询间隔时间
     */
    private final long POLLING_INTERVAL = 10;

    private final String LOCK_LUA_SCRIPT = "if (redis.call('exists', KEYS[1]) == 0) then " +
            "redis.call('hset', KEYS[1], ARGV[2], 1); " +
            "redis.call('pexpire', KEYS[1], ARGV[1]); " +
            "return nil; " +
            "end; " +
            "if (redis.call('hexists', KEYS[1], ARGV[2]) == 1) then " +
            "redis.call('hincrby', KEYS[1], ARGV[2], 1); " +
            "redis.call('pexpire', KEYS[1], ARGV[1]); " +
            "return nil; " +
            "end; " +
            "return redis.call('pttl', KEYS[1]);";

    private final String UNLOCK_LUA_SCRIPT = "if (redis.call('exists', KEYS[1]) == 0) then " +
            "return 1; " +
            "end;" +
            "if (redis.call('hexists', KEYS[1], ARGV[2]) == 0) then " +
            "return nil;" +
            "end; " +
            "local counter = redis.call('hincrby', KEYS[1], ARGV[2], -1); " +
            "if (counter > 0) then " +
            "redis.call('pexpire', KEYS[1], ARGV[1]); " +
            "return 0; " +
            "else " +
            "redis.call('del', KEYS[1]); " +
            "return 1; " +
            "end; " +
            "return nil;";

    public SimpleRedisLockForLettuce(String path) {
        this.path = path;
        this.redisTemplate = SpringUtils.getBean("redisTemplate", RedisTemplate.class);
    }

    public boolean tryLock(long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException {
        waitTime = timeUnit.toMillis(waitTime);
        leaseTime = timeUnit.toMillis(leaseTime);
        long beginTime = System.currentTimeMillis();
        //执行加锁脚本,返回null则获取锁成功
        Object result = LockScript(leaseTime, timeUnit);
        this.leaseTimeTemp = leaseTime;

        //当加锁脚本返回不为null并且还需要继续等待获取锁时执行while语句(轮询获取锁)
        while (result != null
                && (waitTime == -1
                || beginTime + waitTime > System.currentTimeMillis())) {
            log.info("未能抢到锁，轮询ing...");
            Thread.sleep(POLLING_INTERVAL);
            result = LockScript(leaseTime, timeUnit);
        }

        return result == null;
    }

    private Object LockScript(long leaseTime, TimeUnit timeUnit) {
        List<String> keys = Collections.singletonList(path);
        List<String> args = new ArrayList<>();
        args.add(String.valueOf(timeUnit.toMillis(leaseTime)));
        args.add(getCurrentThreadID());
        return execute(LOCK_LUA_SCRIPT, keys, args);
    }

    public void unLock() {
        Object result = unLockScript();
        if (result == null) {
            log.info("解锁失败:redis未上该锁");
        } else if (1 == (long) result) {
            log.info("解锁成功");
        } else if (0 == (long) result) {
            log.info("减少重入次数，并且刷新了锁定时间");
        }
    }

    private Object unLockScript() {
        List<String> keys = Collections.singletonList(path);
        ArrayList<String> args = new ArrayList<>();
        args.add(String.valueOf(leaseTimeTemp));
        args.add(getCurrentThreadID());
        return execute(UNLOCK_LUA_SCRIPT, keys, args);
    }

    private Object execute(String script, List<String> keys, List<String> args) {
        return redisTemplate.execute((RedisCallback<Object>) (redisConnection) -> {
            Object nativeConnection = redisConnection.getNativeConnection();

            Object result = null;

           /* // 单机模式
            if (nativeConnection instanceof RedisAsyncCommands) {
                result = ((RedisAsyncCommands) nativeConnection).getStatefulConnection().async().eval(script, INTEGER, keys.toArray(), args.toArray());
//                result = statefulConnection.(script, keys, args);
                System.out.println(result);
            }
            // 集群模式
            else if (nativeConnection instanceof Jedis) {
                result = ((Jedis) nativeConnection).eval(script, keys, args);
            }*/

            return result;
        });
    }

    private String getCurrentThreadID() {
        return "THREAD_ID:" + (SERVER_ID + "-" + Thread.currentThread().getId()).hashCode();
    }

}
