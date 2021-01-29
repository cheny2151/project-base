package com.cheney.filter;

import cn.cheny.toolbox.redis.client.impl.JsonRedisClient;
import com.cheney.constants.RedisKey;
import com.cheney.utils.CurrentUserHolder;
import com.cheney.utils.jwt.JwtPrincipal;
import com.cheney.utils.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * token拦截器
 * 若拦截到含有token的请求头 则尝试进行认证
 */
@Slf4j
@Component
public class JsonWebTokenFilter extends OncePerRequestFilter {

    public static final String TOKEN_PRE = "Bearer ";

    private static final String AUTH_REQUEST_HEAD = "Authorization";

    @Resource
    private JsonRedisClient<JwtPrincipal> jsonRedisClient;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        // 从请求头中获取token
        String token = httpServletRequest.getHeader(AUTH_REQUEST_HEAD);
        token = extractToken(token);
        if (StringUtils.isNotEmpty(token)) {
            // 校验并解析token
            JwtUtils jwtUtils = JwtUtils.parseToken(token);
            if (jwtUtils != null && jwtUtils.validate()) {
                JwtPrincipal loginUser = loadUserByJwt(jwtUtils);
                // 缓存线程变量：当前登录用户
                if (loginUser != null) {
                    CurrentUserHolder.setCurrentUser(loginUser);
                }
            } else {
                log.warn("用户token校验失败:token:{}", token);
            }
        }

        doFilter(httpServletRequest, httpServletResponse, filterChain);

        // 移除当前登录对象缓存
        CurrentUserHolder.remove();
    }

    private String extractToken(String token) {
        if (token == null || !token.startsWith(TOKEN_PRE)) {
            return null;
        }
        return token.substring(TOKEN_PRE.length());
    }

    /**
     * 根据jwt获取用户认证信息
     *
     * @param jwtUtils 用户jwt信息
     */
    private JwtPrincipal loadUserByJwt(JwtUtils jwtUtils) {
        String token = jwtUtils.getToken();
        return jsonRedisClient.getValue(RedisKey.AUTH_TOKEN_KEY.getKey(token));
    }

}
