package com.cheney.service.impl;

import com.cheney.dao.mybatis.BaseMapper;
import com.cheney.dao.mybatis.UserMapper;
import com.cheney.entity.dto.AuthUser;
import com.cheney.service.UserService;
import com.cheney.utils.jwt.JwtPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * User - serviceImpl
 */
@Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl<AuthUser, Long> implements UserService {

    @Resource(name = "userMapper")
    private UserMapper userMapper;

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
        return null;
    }

}
