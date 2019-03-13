package com.cheney.controller;

import com.alibaba.fastjson.JSONObject;
import com.cheney.dao.mybatis.UserMapper;
import com.cheney.entity.dto.AuthUser;
import com.cheney.redis.client.RedisClient;
import com.cheney.system.message.JsonMessage;
import com.cheney.system.protocol.BaseRequest;
import com.cheney.utils.RequestParamHolder;
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
        BaseRequest<JSONObject> jsonObjectBaseRequest = RequestParamHolder.currentRequestParam();
        System.out.println(jsonObjectBaseRequest);
        System.out.println(redisClient.getValue("test"));
        AuthUser byUsername = userMapper.findByUsername("test");
        System.out.println(byUsername);
        return JsonMessage.success("123");
    }

}
