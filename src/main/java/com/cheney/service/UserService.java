package com.cheney.service;

import com.cheney.entity.jpa.AuthUser;
import com.cheney.utils.jwt.JwtPrincipal;

public interface UserService extends BaseService<AuthUser, Long> {

    /**
     * 通过username查找user
     */
    AuthUser findByUsername(String username);

    /**
     * 登陆认证
     */
    JwtPrincipal authenticated(String username, String password);

}
