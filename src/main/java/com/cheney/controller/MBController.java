package com.cheney.controller;

import com.cheney.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller("mBController")
@RequestMapping(value = "/mybatis")
public class MBController {

    @Autowired
    UserService userService;

    @RequestMapping("/test")
    public void test(){
        System.out.println("=========come in==========");
//        userService.delete(new Long[]{1L,2L});
        userService.delete(1L);
//        System.out.println(user.getUsername());
    }
}
