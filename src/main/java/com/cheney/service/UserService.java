package com.cheney.service;

import com.cheney.entity.dto.AuthUser;
import com.cheney.utils.jwt.JwtPrincipal;

public interface UserService extends BaseService<AuthUser, Long> {

    /**
     * 登陆认证
     */
    JwtPrincipal authenticated(String username, String password);

}
