package com.cheney.controller;

import com.cheney.dao.mybatis.UserMapper;
import com.cheney.entity.dto.AuthUser;
import com.cheney.redis.RedisClient;
import com.cheney.system.message.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller("pageCommonController")
public class PageCommonController {

    @Resource(name = "userMapper")
    private UserMapper userMapper;

    @Resource(name = "strRedisClient")
    private RedisClient<String> redisClient;

    @Autowired
    private Environment environment;

    @RequestMapping("/test")
    @ResponseBody
    public JsonMessage test() {
        System.out.println(redisClient.getValue("test"));
        AuthUser byUsername = userMapper.findByUsername("test");
        System.out.println(byUsername);
        return JsonMessage.success("123");
    }

}
