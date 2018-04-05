package com.cheney.service.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface MyUserDetailsService extends UserDetailsService {

    UserDetails loadUserByToken(String token);

}
