package com.cheney.controller.user.common;

import com.alibaba.fastjson.JSONObject;
import com.cheney.javaconfig.redis.RedisKey;
import com.cheney.redis.client.impl.JsonRedisClient;
import com.cheney.service.UserService;
import com.cheney.system.response.JsonMessage;
import com.cheney.utils.RequestParamHolder;
import com.cheney.utils.jwt.JwtPrincipal;
import com.cheney.utils.jwt.JwtUtils;
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
    @Resource(name = "jsonRedisClient")
    private JsonRedisClient<JwtPrincipal> redisClient;

    /**
     * 登陆
     */
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    @ResponseBody
    public JsonMessage login() {
        JSONObject requestParams = RequestParamHolder.currentParam();
        if (!requestParams.containsKey("username")) {
            return JsonMessage.error("username must not null");
        }
        if (!requestParams.containsKey("password")) {
            return JsonMessage.error("password must not null");
        }
        JwtPrincipal jwtPrincipal = userService.authenticated(requestParams.getString("username"),
                requestParams.getString("password"));
        if (jwtPrincipal != null) {
            String token = JwtUtils.generateToken(jwtPrincipal);
            redisClient.setValue(RedisKey.AUTH_TOKEN_KEY.getKey(token), jwtPrincipal, JwtUtils.IN_DATE);
            return JsonMessage.success(
                    "user", JsonMessage.extract(jwtPrincipal, "username", "roles"),
                    "token", token
            );
        }
        return JsonMessage.error("登陆失败");
    }

}
