package com.cheney.constants;

public enum RedisKey {

    // 用户token key
    AUTH_TOKEN_KEY("AUTH.TOKEN:%s"),

    // 用户username于token 映射关系key
    USER_TOKEN("USER.TOKEN"),

    // 集群任务base key
    CLUSTER_TASK_PRE_KEY("CLUSTER_TASK:"),

    // 集群任务channel
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
