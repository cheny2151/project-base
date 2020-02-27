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
    public String INIT = "redis.call('HSET',KEYS[1],'path',ARGV[1],'max_permits',ARGV[2],'rate',ARGV[3]);return 1;";

}
