package com.cheney.controller;

import com.cheney.entity.User;
import com.cheney.redis.RedisClient;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Date;

@Controller
public class TestController {

    @Resource(name = "jdkRedisClient")
    private RedisClient<User> redisClient;

    @RequestMapping("/test")
    public void test() {
        User user = new User();
        user.setUsername("test");
        redisClient.removeKey("test");
//        System.out.println(redisClient.getList("Test"));
    }

}
