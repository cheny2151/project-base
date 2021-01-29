package com.cheney.utils;

import com.cheney.exception.BusinessRunTimeException;
import com.cheney.system.protocol.ResponseCode;
import com.cheney.utils.jwt.JwtPrincipal;

/**
 * 登录用户Holder类
 *
 * @author cheney
 * @date 2019-08-29
 */
public class CurrentUserHolder {

    private static final ThreadLocal<JwtPrincipal> authUserThreadLocal = new ThreadLocal<>();

    public static JwtPrincipal getCurrentUser() {
        return authUserThreadLocal.get();
    }

    public static JwtPrincipal requiredCurrentUser() {
        JwtPrincipal user = getCurrentUser();
        if (user == null){
            throw new BusinessRunTimeException(ResponseCode.USER_NOT_LOGIN);
        }
        return user;
    }

    public static void setCurrentUser(JwtPrincipal authUser) {
        authUserThreadLocal.set(authUser);
    }

    public static void remove() {
        authUserThreadLocal.remove();
    }

}
