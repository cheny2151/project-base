package com.cheney.filter;

import com.alibaba.fastjson.JSON;
import com.cheney.entity.Role;
import com.cheney.service.RoleService;
import com.cheney.system.protocol.BaseResponse;
import com.cheney.system.protocol.ResponseCode;
import com.cheney.utils.CurrentUserHolder;
import com.cheney.utils.HttpSupport;
import com.cheney.utils.URLUtils;
import com.cheney.utils.jwt.JwtPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import java.util.Objects;

/**
 * 角色权限过滤器
 *
 * @author cheney
 * @date 2019-11-10
 */
@Slf4j
@Component
public class RolePermissionFilter extends OncePerRequestFilter {

    @Value("${user.auth.urlPatterns:''}")
    private String[] authUrlPatterns;
    @Value("${user.permission.ignoreUrls:''}")
    private String[] ignoreUrls;
    @Resource(name = "roleServiceImpl")
    private RoleService roleService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = httpServletRequest.getRequestURI();

        // 登陆验证
        boolean pass = !URLUtils.matchesUrl(uri, authUrlPatterns);
        JwtPrincipal currentUser = CurrentUserHolder.getCurrentUser();
        if (!pass && currentUser == null) {
            log.info("请求url:{},用户未登录", uri);
            writeUnauthorized(httpServletResponse);
            return;
        }

        // 匹配忽略权限路径
        if (!pass) {
            pass = URLUtils.matchesUrl(uri, ignoreUrls);
        }

        // 权限校验
        if (!pass && !CollectionUtils.isEmpty(currentUser.getRoles())) {
            // 从缓存中获取角色对应的权限url并判断当前登录用户是否有访问权限
            pass = currentUser.getRoles().stream().map(role -> roleService.getByCache(role))
                    .filter(Objects::nonNull)
                    .map(Role::getUrlPatterns).flatMap(Collection::stream)
                    .anyMatch(urlPattern -> uri.matches(URLUtils.fixPattern(urlPattern)));
        }

        if (!pass) {
            writeForbidden(httpServletResponse);
            return;
        }

        doFilter(httpServletRequest, httpServletResponse, filterChain);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        // OPTIONS方法不执行权限拦截
        return request.getMethod().equals(HttpSupport.Method.HTTP_METHOD_OPTIONS);
    }

    /**
     * 响应未登陆访问
     *
     * @param httpServletResponse 响应
     */
    private void writeUnauthorized(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.setHeader("Content-Type", "application/json;charset=utf-8");
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        BaseResponse<?> responseBody = BaseResponse.error(ResponseCode.USER_NOT_LOGIN);
        JSON.writeJSONString(outputStream, responseBody);
        outputStream.flush();
    }

    /**
     * 响应无权限访问
     *
     * @param httpServletResponse 响应
     */
    private void writeForbidden(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setStatus(HttpStatus.OK.value());
        httpServletResponse.setHeader("Content-Type", "application/json;charset=utf-8");
        ServletOutputStream outputStream = httpServletResponse.getOutputStream();
        BaseResponse<?> responseBody = BaseResponse.error(ResponseCode.FORBIDDEN);
        JSON.writeJSONString(outputStream, responseBody);
        outputStream.flush();
    }

}
