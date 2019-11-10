package com.cheney.javaconfig.redis;

/**
 * redis缓存基础key
 *
 * @author cheney
 * @date 2019-11-10
 */
public enum CacheBaseKey {

    ROLE_KEY("CACHE:ROLE");

    private final static String KEY_PRE = "BASE:";

    private String key;

    CacheBaseKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return KEY_PRE + key;
    }

}
