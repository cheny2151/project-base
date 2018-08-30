package com.cheney.controller;

import com.cheney.dao.mybatis.UserMapper;
import com.cheney.entity.dto.AuthUser;
import com.cheney.system.message.JsonMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

@Controller("pageCommonController")
public class PageCommonController {

    @Resource(name = "userMapper")
    private UserMapper userMapper;

    @RequestMapping("/test")
    @ResponseBody
    public JsonMessage test() {
        AuthUser byUsername = userMapper.findByUsername("123");
        System.out.println(byUsername);
        return JsonMessage.success("123");
    }

}
