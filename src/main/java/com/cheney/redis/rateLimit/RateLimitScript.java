package com.cheney.redis.rateLimit;

/**
 * 令牌桶算法脚本
 *
 * @author cheney
 * @date 2020-02-26
 */
public class RateLimitScript {

    /**
     * 初始化脚本
     * path：限流路径,max_permits：最大令牌个数；rate：每秒新增令牌数
     */
    public String INIT = "redis.call('HMSET',KEYS[1],'path',ARGV[1],'max_permits',ARGV[2],'rate',ARGV[3]);return 1;";

    public String GET_TOKEN =
            "local rate_limit = redis.call('HMGET', 'last_time','permits','rate','max_permits');" +
            "local last_time = rate_limit[1];" +
            "local permits = rate_limit[2];" +
            "local rate = rate_limit[3];" +
            "local max_permits = rate_limit[4];" +
            "redis.replicate_commands();" +
            "local cur_time = redis.call('time')[1]; " +
            "local expect_permits = max_permits; " +
            "if (last_time ~= nil) then local add_permits = (cur_time - last_time) * rate; " +
            "if (add_permits > 0) then redis.call('HSET', 'last_time', cur_time); " +
            "end " +
            "expect_permits = math.min(add_permits + permits, max_permits);" +
            "else redis.call('HSET', 'last_time', cur_time);" +
            "end" +
            "if expect_permits < ARVG[1] then redis.call('HSET', 'permits', expect_permits);" +
            "return -1" +
            "else " +
            "redis.call('HSET', 'permits', expect_permits - ARVG[1]); " +
            "return 1 end";

}
