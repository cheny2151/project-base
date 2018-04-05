package com.cheney.controller;

import com.cheney.entity.AuthUser;
import com.cheney.redis.RedisClient;
import com.cheney.service.UserService;
import com.cheney.system.message.JsonMessage;
import com.cheney.system.page.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller
public class TestController {

    @Resource(name = "jdkRedisClient")
    private RedisClient<AuthUser> redisClient;
    @Resource(name = "userServiceImpl")
    private UserService userService;

    @RequestMapping("/test")
    @ResponseBody
    public JsonMessage test(Pageable<AuthUser> pageable) {
        return null;
    }

}
