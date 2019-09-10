package com.cheney.javaconfig.redis;

public enum RedisKey {

    AUTH_TOKEN_KEY("auth.token:%s"),

    CLUSTER_TASK_PRE_KEY("CLUSTER_TASK:"),

    CLUSTER_TASK_CHANNEL_PRE_KEY("CLUSTER_TASK_CHANNEL:");

    private final static String KEY_PRE = "BASE:";

    private String key;

    RedisKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return KEY_PRE + key;
    }

    public String getKey(String... format) {
        return String.format(getKey(), (Object[]) format);
    }

}
