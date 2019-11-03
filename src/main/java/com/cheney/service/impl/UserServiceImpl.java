package com.cheney.service.impl;

import com.cheney.dao.mybatis.BaseMapper;
import com.cheney.dao.mybatis.UserMapper;
import com.cheney.entity.dto.AuthUser;
import com.cheney.exception.BusinessRunTimeException;
import com.cheney.service.UserService;
import com.cheney.system.response.ResponseCode;
import com.cheney.utils.Md5Utils;
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
    public JwtPrincipal authenticated(String username, String password) {
        AuthUser auth = userMapper.findByUsername(username);
        if (auth == null) {
            throw new BusinessRunTimeException(ResponseCode.USERNAME_OR_PASSWORD_ERROR);
        }
        boolean validate = Md5Utils.getSaltverifyMD5(password, auth.getPassword());
        if (!validate) {
            throw new BusinessRunTimeException(ResponseCode.USERNAME_OR_PASSWORD_ERROR);
        }
        return new JwtPrincipal(username, password, true, null, auth.getOriginId(), null);
    }

}
