package com.cheney.filter;

import com.alibaba.fastjson.JSON;
import com.cheney.entity.dto.AuthUser;
import com.cheney.redis.client.impl.JsonRedisClient;
import com.cheney.system.message.JsonMessage;
import com.cheney.system.message.ResponseCode;
import com.cheney.utils.jwt.JwtPrincipal;
import com.cheney.utils.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * token拦截器
 * 若拦截到含有token的请求头 则尝试进行认证
 */
@Slf4j
@WebFilter
public class JsonWebTokenFilter extends OncePerRequestFilter {

    @Resource(name = "jsonRedisClient")
    private JsonRedisClient<JwtPrincipal> redisClient;
    @Value("${user.auth.urlPatterns}")
    private String urlPatterns;

    private static final String AUTH_REQUEST_HEAD = "AUTH_TOKEN";

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        // 从请求头中获取token
        String token = httpServletRequest.getHeader(AUTH_REQUEST_HEAD);

        Optional<AuthUser> loginUser = Optional.empty();
        if (StringUtils.isNotEmpty(token)) {
            // 校验并解析token
            JwtUtils jwtUtils = JwtUtils.parseToken(token);
            if (jwtUtils != null && jwtUtils.validate()) {
                loginUser = Optional.ofNullable(loadUserByJwt(jwtUtils));
            } else {
                log.error("用户token校验失败:token:{}", token);
            }
        }

        String requestURI = httpServletRequest.getRequestURI();
        if (requiredLogin(requestURI) && !loginUser.isPresent()) {
            log.info("请求url:{},用户未登录", requestURI);
            httpServletResponse.setHeader("Content-Type", "application/json;charset=utf-8");
            JsonMessage errorMsg = JsonMessage.error(ResponseCode.USER_NOT_LOGIN);
            ServletOutputStream outputStream = httpServletResponse.getOutputStream();
            outputStream.write(JSON.toJSONBytes(errorMsg));
            outputStream.flush();
            return;
        }

        doFilter(httpServletRequest, httpServletResponse, filterChain);

    }

    /**
     * 根据jwt获取用户认证信息
     *
     * @param jwtUtils 用户jwt信息
     */
    private AuthUser loadUserByJwt(JwtUtils jwtUtils) {
        return null;
    }

    /**
     * 判断是否需要登录状态才可以访问
     *
     * @param url 请求url
     * @return required login
     */
    private boolean requiredLogin(String url) {
        if (StringUtils.isEmpty(this.urlPatterns)) {
            return false;
        }
        String[] urlPatterns = this.urlPatterns.split(",");
        for (String urlPattern : urlPatterns) {
            urlPattern = urlPattern.endsWith("*") ?
                    fixPattern(urlPattern)
                    : urlPattern;
            if (url.matches(urlPattern)) {
                return true;
            }
        }
        return false;
    }

    private static String fixPattern(String pattern) {
        int length = pattern.length();
        return ".*".equals(pattern.substring(length - 2)) ? pattern : pattern.substring(0, length - 1) + ".*";
    }

}
