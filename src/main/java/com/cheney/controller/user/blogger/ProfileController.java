package com.cheney.controller.user.blogger;

import com.cheney.service.BloggerService;
import com.cheney.system.message.JsonMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 个人中心controller
 */
@Controller("bloggerProfileController")
@RequestMapping(value = "/blogger/profile")
public class ProfileController {

    @Resource(name = "bloggerServiceImpl")
    private BloggerService bloggerService;

    /**
     * 个人信息
     */
    @RequestMapping(value = "/info", method = RequestMethod.GET)
    @ResponseBody
    public JsonMessage info() {
        return JsonMessage.success(
                bloggerService.getCurrent()
        );
    }

}
