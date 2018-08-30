package com.cheney.filter;

import com.cheney.service.security.MyUserDetailsService;
import com.cheney.utils.jwt.JwtPrincipal;
import com.cheney.utils.jwt.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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
public class JsonWebTokenFilter extends OncePerRequestFilter {

    private static final String AUTH_REQUEST_HEAD = "AUTH_TOKEN";

    @Resource(name = "userDetailsServiceImpl")
    private MyUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        //从请求头中获取token
        String token = httpServletRequest.getHeader(AUTH_REQUEST_HEAD);

        if (StringUtils.isNotEmpty(token)) {
            //redis中获取认证或直接解析token获取
            UserDetails userDetails = (userDetails = userDetailsService.loadUserByToken(token)) != null ? userDetails : loadUserByToken(token);

            if (userDetails != null && JwtUtils.validate(token, (JwtPrincipal) userDetails)) {
                SecurityContext securityContext = SecurityContextHolder.getContext();
                //UsernamePasswordAuthenticationToken :
                // 参数1：principal（安全认证信息类,即JwtPrincipal）2: 3:角色权限信息authorities
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(
                        httpServletRequest));
                securityContext.setAuthentication(authentication);
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    /**
     * 直接解析token从数据库中获取认证
     *
     * @param token token
     */
    private UserDetails loadUserByToken(String token) {
        String username = JwtUtils.parseToUsername(token);
        UserDetails details = null;
        try {
            details = userDetailsService.loadUserByUsername(username);
        } catch (UsernameNotFoundException e) {
            log.warn("token exist,but username not found");
        }
        return details;
    }

}
