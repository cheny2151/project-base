package com.cheney.utils.jwt;

import lombok.Data;

import java.util.Date;

/**
 * JsonWebToken 安全认证用户信息
 */
@Data
public class JwtPrincipal {

    private static final long serialVersionUID = 2764317447462499613L;

    private String username;

    private String password;

    private boolean accountNonExpired;

    private boolean accountNonLocked;

    private boolean enabled;

    private Date lastPasswordReset;

    public JwtPrincipal(String username, String password, Date lastPasswordReset, boolean enabled) {
        this.username = username;
        this.password = password;
        this.lastPasswordReset = lastPasswordReset;
        this.enabled = enabled;
        this.accountNonExpired = true;
        this.accountNonLocked = true;
    }

}
