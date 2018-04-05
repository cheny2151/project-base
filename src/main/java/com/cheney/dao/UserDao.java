package com.cheney.dao;

import com.cheney.entity.AuthUser;

public interface UserDao extends BaseDao<AuthUser, Long> {

    AuthUser findByUsername(String username);

}
