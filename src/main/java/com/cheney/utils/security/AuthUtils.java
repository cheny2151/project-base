package com.cheney.utils.security;

import com.cheney.utils.jwt.JwtPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;

/**
 * 获取认证信息工具类
 */
public final class AuthUtils {

    /**
     * 获取当前完成认证的用户username
     */
    public static String getCurrentUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    /**
     * 获取当前完成认证的用户完整认证信息
     */
    public static JwtPrincipal getCurrentPrincipal() {
        return (JwtPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * 获取当前完成认证的用户的所有权限
     */
    public static Collection<? extends GrantedAuthority> getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }

}
