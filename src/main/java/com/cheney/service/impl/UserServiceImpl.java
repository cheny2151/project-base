package com.cheney.service.impl;

import com.cheney.dao.BaseDao;
import com.cheney.dao.UserDao;
import com.cheney.entity.User;
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
public class UserServiceImpl extends BaseServiceImpl<User, Long> implements UserService {

    @Resource(name = "userDaoImpl")
    private UserDao userDao;
    @Resource(name = "userDetailsServiceImpl")
    private UserDetailsService userDetailsService;
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    @Override
    protected void setBaseDao(BaseDao<User, Long> baseDao) {
        super.setBaseDao(baseDao);
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public JwtPrincipal authenticated(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        Authentication authenticate = authenticationManager.authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authenticate);
        return (JwtPrincipal) userDetailsService.loadUserByUsername(username);
    }


}
