package com.cheney.controller.user;

import com.alibaba.fastjson.JSONObject;
import com.cheney.javaconfig.redis.RedisKey;
import com.cheney.redis.client.impl.JsonRedisClient;
import com.cheney.service.AuthUserService;
import com.cheney.system.response.JsonMessage;
import com.cheney.utils.CurrentUserHolder;
import com.cheney.utils.RequestParamHolder;
import com.cheney.utils.jwt.JwtPrincipal;
import com.cheney.utils.jwt.JwtUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 用户统一登陆注册接口
 */
@RestController("authController")
@RequestMapping("/auth")
public class AuthController {

    @Resource(name = "authUserServiceImpl")
    private AuthUserService authUserService;
    @Resource(name = "jsonRedisClient")
    private JsonRedisClient<JwtPrincipal> redisClient;

    /**
     * 登陆
     */
    @PostMapping(value = "/login")
    public JsonMessage login() {
        JSONObject requestParams = RequestParamHolder.currentParam();
        if (!requestParams.containsKey("username")) {
            return JsonMessage.error("username must not null");
        }
        if (!requestParams.containsKey("password")) {
            return JsonMessage.error("password must not null");
        }
        JwtPrincipal jwtPrincipal = authUserService.authenticated(requestParams.getString("username"),
                requestParams.getString("password"));
        if (jwtPrincipal != null) {
            String token = jwtPrincipal.getToken();
            redisClient.setValue(RedisKey.AUTH_TOKEN_KEY.getKey(token), jwtPrincipal, JwtUtils.IN_DATE);
            return JsonMessage.success(
                    "user", JsonMessage.extract(jwtPrincipal, "username", "roles"),
                    "token", token
            );
        }
        return JsonMessage.error("登陆失败");
    }

    /**
     * 登出
     */
    @DeleteMapping("/logout")
    public JsonMessage logout() {
        String currentToken = CurrentUserHolder.getCurrentUser().getToken();
        redisClient.removeKey(RedisKey.AUTH_TOKEN_KEY.getKey(currentToken));
        return JsonMessage.success();
    }

}
