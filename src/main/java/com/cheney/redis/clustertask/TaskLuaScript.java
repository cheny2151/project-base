package com.cheney.redis.clustertask;

/**
 * lua脚本
 *
 * @author cheney
 * @date 2019-09-03
 */
public class TaskLuaScript {

    public static final String ADD_STEP_LUA_SCRIPT =
            "if (redis.call('exists', KEYS[1]) == 1) then " +
                    "local step = redis.call('hincrby', KEYS[1], KEYS[2], 1); " +
                    "return step - 1; " +
                    "end;" +
                    "return nil; ";
}
