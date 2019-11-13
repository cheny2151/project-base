package com.cheney.service;

import com.cheney.entity.AuthUser;
import com.cheney.utils.jwt.JwtPrincipal;

public interface AuthUserService extends BaseService<AuthUser, Long> {

    /**
     * 用户登录认证
     *
     * @param username 用户名
     * @param password 密码
     * @return 认证令牌
     */
    JwtPrincipal authenticated(String username, String password);

    /**
     * 重置token
     */
    void resetToken(JwtPrincipal jwtPrincipal);

    /**
     * 删除token
     */
    void removeToken(String username);

}
