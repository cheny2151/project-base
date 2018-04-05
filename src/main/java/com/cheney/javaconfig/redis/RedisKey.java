package com.cheney.javaconfig.redis;

import org.junit.Test;

public class RedisKey {

    public final static String AUTH_TOKEN_KEY = "auth.details.%s";

    @Test
    public void test(){
        String test = String.format(AUTH_TOKEN_KEY, "test");
        System.out.println(test);
    }

}
