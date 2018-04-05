package com.cheney.utils.security;

import com.cheney.entity.AuthUser;
import com.cheney.entity.Role;
import com.cheney.utils.jwt.JwtPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * UserDetails工厂
 * 创建
 */
public class UserDetailsFactory {

    private static final String ROLE_FLAG = "ROLE_";

    private UserDetailsFactory() {
    }

    public static UserDetails create(AuthUser user) {
        return new JwtPrincipal(asAuthorities(user.getRoles()), user.getUsername(), user.getPassword(), user.getLastPasswordReset(), user.isEnabled());
    }

    private static Collection<? extends GrantedAuthority> asAuthorities(Set<Role> roles) {
        ArrayList<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(ROLE_FLAG + role.getAuth()));
        }
        return authorities;
    }

}
