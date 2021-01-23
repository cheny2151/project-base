package com.cheney.filter;

import com.alibaba.fastjson.JSON;
import com.cheney.constants.RedisKey;
import com.cheney.redis.client.impl.JsonRedisClient;
import com.cheney.system.protocol.JsonMessage;
import com.cheney.system.protocol.ResponseCode;
import com.cheney.utils.CurrentUserHolder;
import com.cheney.utils.URLUtils;
import com.cheney.utils.jwt.JwtPrincipal;
import com.cheney.utils.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * token拦截器
 * 若拦截到含有token的请求头 则尝试进行认证
 */
@Slf4j
@Component
public class JsonWebTokenFilter extends OncePerRequestFilter {

    @Resource(name = "jsonRedisClient")
    private JsonRedisClient<JwtPrincipal> redisClient;
    @Value("${user.auth.urlPatterns}")
    private String[] urlPatterns;

    private static final String AUTH_REQUEST_HEAD = "AUTH_TOKEN";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        // 从请求头中获取token
        String token = httpServletRequest.getHeader(AUTH_REQUEST_HEAD);

        Optional<JwtPrincipal> loginUser = Optional.empty();
        if (StringUtils.isNotEmpty(token)) {
            // 校验并解析token
            JwtUtils jwtUtils = JwtUtils.parseToken(token);
            if (jwtUtils != null && jwtUtils.validate()) {
                loginUser = Optional.ofNullable(loadUserByJwt(jwtUtils));
                // 缓存线程变量：当前登录用户
                loginUser.ifPresent(CurrentUserHolder::setCurrentUser);
            } else {
                log.error("用户token校验失败:token:{}", token);
            }
        }

        String requestURI = httpServletRequest.getRequestURI();
        if (URLUtils.matchesUrl(requestURI, urlPatterns) && loginUser.isEmpty()) {
            log.info("请求url:{},用户未登录", requestURI);
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setHeader("Content-Type", "application/json;charset=utf-8");
            JsonMessage errorMsg = JsonMessage.error(ResponseCode.USER_NOT_LOGIN);
            ServletOutputStream outputStream = httpServletResponse.getOutputStream();
            outputStream.write(JSON.toJSONBytes(errorMsg));
            outputStream.flush();
            return;
        }

        doFilter(httpServletRequest, httpServletResponse, filterChain);

        // 移除当前登录对象缓存
        CurrentUserHolder.remove();

    }

    /**
     * 根据jwt获取用户认证信息
     *
     * @param jwtUtils 用户jwt信息
     */
    private JwtPrincipal loadUserByJwt(JwtUtils jwtUtils) {
        String token = jwtUtils.getToken();
        return redisClient.getValue(RedisKey.AUTH_TOKEN_KEY.getKey(token));
    }

}
