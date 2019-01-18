package com.cheney.redis.lock;

/**
 * @author cheney
 */
public class LockConstant {

    public static final String AWAKEN_LOCK_LUA_SCRIPT = "if (redis.call('exists', KEYS[1]) == 0) then " +
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

    public static final String AWAKEN_UNLOCK_LUA_SCRIPT = "if (redis.call('exists', KEYS[1]) == 0) then " +
            "redis.call('publish', KEYS[2], ARGV[1]); " +
            "return 1; " +
            "end;" +
            "if (redis.call('hexists', KEYS[1], ARGV[3]) == 0) then " +
            "return nil;" +
            "end; " +
            "local counter = redis.call('hincrby', KEYS[1], ARGV[3], -1); " +
            "if (counter > 0) then " +
            "redis.call('pexpire', KEYS[1], ARGV[2]); " +
            "return 0; " +
            "else " +
            "redis.call('del', KEYS[1]); " +
            "redis.call('publish', KEYS[2], ARGV[1]); " +
            "return 1; " +
            "end; " +
            "return nil;";

    public static final String LOCK_LUA_SCRIPT = "if (redis.call('exists', KEYS[1]) == 0) then " +
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

    public static final String UNLOCK_LUA_SCRIPT = "if (redis.call('exists', KEYS[1]) == 0) then " +
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

    public static final String LOCK_CHANNEL = "LOCK_CHANNEL";

    public static final String UNLOCK_MESSAGE = "UN_LOCK";

}
