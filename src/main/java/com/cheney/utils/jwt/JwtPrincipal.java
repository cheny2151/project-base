package com.cheney.utils.jwt;

import com.cheney.entity.dto.AuthUser;
import com.cheney.entity.dto.Role;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.Set;

/**
 * JsonWebToken 安全认证用户信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JwtPrincipal extends AuthUser {

    private static final long serialVersionUID = 2764317447462499613L;

    public JwtPrincipal() {
    }

    public JwtPrincipal(String username, String password, boolean enabled, Set<Role> roles, Long originId, Date lastPasswordReset) {
        super(username, password, enabled, roles, originId, lastPasswordReset);
    }

}
