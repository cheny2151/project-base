package com.cheney.service.impl;

import com.cheney.dao.mybatis.BaseMapper;
import com.cheney.dao.mybatis.UserMapper;
import com.cheney.entity.dto.AuthUser;
import com.cheney.service.UserService;
import com.cheney.utils.jwt.JwtPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * User - serviceImpl
 */
@Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl<AuthUser, Long> implements UserService {

    @Autowired
    private UserMapper userMapper;
    @Resource(name = "userDetailsServiceImpl")
    private UserDetailsService userDetailsService;
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    @Override
    protected void setBaseMapper(BaseMapper<AuthUser, Long> baseMapper) {
        super.setBaseMapper(baseMapper);
    }

    @Override
    public AuthUser findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public JwtPrincipal authenticated(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        return (JwtPrincipal) userDetailsService.loadUserByUsername(username);
    }

}
