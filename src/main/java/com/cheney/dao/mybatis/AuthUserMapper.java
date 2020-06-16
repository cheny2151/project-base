package com.cheney.dao.mybatis;

import com.cheney.entity.AuthUser;
import org.springframework.stereotype.Repository;

public interface AuthUserMapper extends BaseMapper<AuthUser, Long> {

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户
     */
    AuthUser findByUsername(String username);
}
