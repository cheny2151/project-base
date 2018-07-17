package com.cheney.service.security;

import com.cheney.dao.UserMapper;
import com.cheney.entity.dto.AuthUser;
import com.cheney.javaconfig.redis.RedisKey;
import com.cheney.redis.RedisClient;
import com.cheney.utils.security.UserDetailsFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 *
 */
@Service("userDetailsServiceImpl")
public class UserDetailsServiceImpl implements MyUserDetailsService {

    @Resource(name = "userMapper")
    private UserMapper userMapper;
    @Resource(name = "jdkRedisClient")
    private RedisClient<UserDetails> redisClient;

    /**
     * 获取认证信息
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AuthUser user = userMapper.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("unknown username");
        }
        return UserDetailsFactory.create(user);
    }

    /**
     * redis获取认证
     * key:token
     */
    public UserDetails loadUserByToken(String token) {
        return redisClient.getValue(String.format(RedisKey.AUTH_TOKEN_KEY, token));
    }

}
