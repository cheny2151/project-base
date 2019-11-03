package com.cheney.utils;

import com.cheney.entity.dto.AuthUser;

/**
 * 登录用户Holder类
 *
 * @author cheney
 * @date 2019-08-29
 */
public class CurrentUserHolder {

    private static ThreadLocal<AuthUser> authUserThreadLocal = new ThreadLocal<>();

    public static AuthUser getCurrentUser() {
        return authUserThreadLocal.get();
    }

    public static void setCurrentUser(AuthUser authUser) {
        authUserThreadLocal.set(authUser);
    }

    public static void remove() {
        authUserThreadLocal.remove();
    }

}
