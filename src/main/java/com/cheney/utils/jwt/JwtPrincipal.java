package com.cheney.utils.jwt;

import com.cheney.entity.AuthUser;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

/**
 * JsonWebToken 安全认证用户信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class JwtPrincipal extends AuthUser {

    private static final long serialVersionUID = 2764317447462499613L;

    private String token;

    public static JwtPrincipal createJwtPrincipal(AuthUser authUser) {
        JwtPrincipal jwtPrincipal = new JwtPrincipal();
        BeanUtils.copyProperties(authUser, jwtPrincipal);
        jwtPrincipal.token = JwtUtils.generateToken(jwtPrincipal);
        return jwtPrincipal;
    }

}
