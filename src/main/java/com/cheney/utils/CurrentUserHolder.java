package com.cheney.utils;

import com.cheney.utils.jwt.JwtPrincipal;

/**
 * 登录用户Holder类
 *
 * @author cheney
 * @date 2019-08-29
 */
public class CurrentUserHolder {

    private static ThreadLocal<JwtPrincipal> authUserThreadLocal = new ThreadLocal<>();

    public static JwtPrincipal getCurrentUser() {
        return authUserThreadLocal.get();
    }

    public static void setCurrentUser(JwtPrincipal authUser) {
        authUserThreadLocal.set(authUser);
    }

    public static void remove() {
        authUserThreadLocal.remove();
    }

}
