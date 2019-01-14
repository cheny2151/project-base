package com.cheney.controller.user.common;

import com.cheney.javaconfig.redis.RedisKey;
import com.cheney.redis.client.RedisClient;
import com.cheney.service.UserService;
import com.cheney.system.message.JsonMessage;
import com.cheney.utils.jwt.JwtPrincipal;
import com.cheney.utils.jwt.JwtUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * 用户统一登陆注册接口
 */
@Controller("authController")
@RequestMapping("/auth")
public class AuthController {

    @Resource(name = "userServiceImpl")
    private UserService userService;
    @Resource(name = "jdkRedisClient")
    private RedisClient<UserDetails> redisClient;
    @Value("${jwt.indate}")
    private int indate;

    /**
     * 登陆
     *
     * @param username 用户名
     * @param password 密码
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public JsonMessage login(String username, String password) {
        if (StringUtils.isEmpty(username)) {
            return JsonMessage.error("username must not null");
        }
        if (StringUtils.isEmpty(password)) {
            return JsonMessage.error("password must not null");
        }
        JwtPrincipal jwtPrincipal = userService.authenticated(username, password);
        if (jwtPrincipal != null) {
            String token = JwtUtils.generateToken(jwtPrincipal);
            redisClient.setValue(String.format(RedisKey.AUTH_TOKEN_KEY, token), jwtPrincipal, indate);
            return JsonMessage.success(
                    "user", JsonMessage.extract(jwtPrincipal, "username", "authorities"),
                    "token", token
            );
        }
        return JsonMessage.error("登陆失败");
    }

}
