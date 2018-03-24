package com.cheney.filter;

import com.cheney.utils.jwt.JwtPrincipal;
import com.cheney.utils.jwt.JwtUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
public class JsonWebTokenFilter extends OncePerRequestFilter {

    private final Logger LOGGER = Logger.getLogger(this.getClass());

    private static final String AUTH_REQUEST_HEAD = "AUTH_TOKEN";

    @Resource(name = "userDetailsServiceImpl")
    private UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        //从请求头中获取token
        String token = httpServletRequest.getHeader(AUTH_REQUEST_HEAD);

        if (StringUtils.isNotEmpty(token)) {
            String username = JwtUtils.parseToUsername(token);
            UserDetails userDetails = null;
            try {
                userDetails = userDetailsService.loadUserByUsername(username);
            } catch (UsernameNotFoundException e) {
                LOGGER.warn("token exist,but username not found");
            }
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

}
