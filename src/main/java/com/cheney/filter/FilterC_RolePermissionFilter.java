package com.cheney.filter;

import com.alibaba.fastjson.JSON;
import com.cheney.entity.Role;
import com.cheney.service.RoleService;
import com.cheney.system.protocol.BaseResponse;
import com.cheney.system.response.ResponseCode;
import com.cheney.utils.CurrentUserHolder;
import com.cheney.utils.URLUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;

/**
 * 角色权限过滤器
 *
 * @author cheney
 * @date 2019-11-10
 */
@WebFilter(urlPatterns = {"/auth/*"})
public class FilterC_RolePermissionFilter extends OncePerRequestFilter {

    @Value("${user.permission.ignoreUrls}")
    private String[] ignoreUrls;
    @Resource(name = "roleServiceImpl")
    private RoleService roleService;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        String uri = httpServletRequest.getRequestURI();
        if (URLUtils.matchesUrl(uri, ignoreUrls)) {
            // 匹配权限忽略路径
            doFilter(httpServletRequest, httpServletResponse, filterChain);
            return;
        }

        // 是否通过权限校验
        boolean pass = false;
        Set<String> roles = CurrentUserHolder.getCurrentUser().getRoles();
        if (!CollectionUtils.isEmpty(roles)) {
            // 从缓存中获取角色对应的权限url并判断当前登录用户是否有访问权限
            pass = roles.stream().map(role -> roleService.getByCache(role)).filter(Objects::nonNull)
                    .map(Role::getUrlPatterns).flatMap(Collection::stream)
                    .anyMatch(urlPattern -> uri.matches(URLUtils.fixPattern(urlPattern)));
        }

        if (!pass) {
            writeUnauthorized(httpServletResponse);
            return;
        }

        doFilter(httpServletRequest, httpServletResponse, filterChain);
    }

    /**
     * 响应无权限访问
     *
     * @param httpServletResponse 响应
     */
    private void writeUnauthorized(HttpServletResponse httpServletResponse) throws IOException {
        httpServletResponse.setStatus(HttpStatus.FORBIDDEN.value());
        httpServletResponse.setHeader("Content-Type", "application/json;charset=utf-8");
        PrintWriter writer = httpServletResponse.getWriter();
        BaseResponse responseBody = BaseResponse.error(ResponseCode.FORBIDDEN);
        JSON.writeJSONString(writer, responseBody);
    }

}
