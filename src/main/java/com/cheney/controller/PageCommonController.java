package com.cheney.controller;

import com.cheney.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

@Controller("pageCommonController")
public class PageCommonController {

    @Resource(name = "userServiceImpl")
    private UserService userService;

    @RequestMapping("/test")
    public String test() {
        return "/index.html";
    }

}
