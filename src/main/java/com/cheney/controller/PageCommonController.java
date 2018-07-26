package com.cheney.controller;

import com.cheney.dao.mybatis.UserMapper;
import com.cheney.system.message.JsonMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller("pageCommonController")
public class PageCommonController {

    @Resource(name = "userMapper")
    private UserMapper userMapper;

    @RequestMapping("/test")
    public JsonMessage test() {
        return JsonMessage.success(userMapper.findByUsername("123"));
    }

}
