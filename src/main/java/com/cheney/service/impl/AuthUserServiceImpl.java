package com.cheney.service.impl;

import cn.cheny.toolbox.redis.client.impl.JsonRedisClient;
import com.cheney.constants.RedisKey;
import com.cheney.dao.mybatis.AuthUserMapper;
import com.cheney.entity.AuthUser;
import com.cheney.exception.BusinessRunTimeException;
import com.cheney.service.AuthUserService;
import com.cheney.system.response.ResponseCode;
import com.cheney.utils.Md5Utils;
import com.cheney.utils.jwt.JwtPrincipal;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * AuthUser - serviceImpl
 */
@Service("authUserServiceImpl")
@Transactional
public class AuthUserServiceImpl extends BaseServiceImpl<AuthUser, Long> implements AuthUserService {

    @Resource
    private AuthUserMapper authUserMapper;
    @Resource(name = "taskExecutor")
    private ThreadPoolTaskExecutor taskExecutor;
    @Resource
    private JsonRedisClient<String> jsonRedisClient;

    @Autowired
    public AuthUserServiceImpl(AuthUserMapper authUserMapper) {
        super(authUserMapper);
    }

    @Override
    public JwtPrincipal authenticated(String username, String password) {
        AuthUser authUser = authUserMapper.findByUsername(username);
        if (authUser == null) {
            throw new BusinessRunTimeException(ResponseCode.USERNAME_OR_PASSWORD_ERROR);
        }
        boolean validate = Md5Utils.getSaltverifyMD5(password, authUser.getPassword());
        if (!validate) {
            throw new BusinessRunTimeException(ResponseCode.USERNAME_OR_PASSWORD_ERROR);
        }
        return JwtPrincipal.createJwtPrincipal(authUser);
    }

    @Override
    public void resetToken(JwtPrincipal jwtPrincipal) {
        taskExecutor.execute(() -> {
            removeToken(jwtPrincipal.getUsername());
            jsonRedisClient.HSetForMap(RedisKey.USER_TOKEN.getKey(), jwtPrincipal.getUsername(), jwtPrincipal.getToken());
        });
    }

    @Override
    public void removeToken(String username) {
        String oldToken = jsonRedisClient.HGetForMap(RedisKey.USER_TOKEN.getKey(), username);
        if (!StringUtils.isEmpty(oldToken)) {
            jsonRedisClient.delete(RedisKey.AUTH_TOKEN_KEY.getKey(oldToken));
            jsonRedisClient.HDel(RedisKey.USER_TOKEN.getKey(), username);
        }
    }

}
