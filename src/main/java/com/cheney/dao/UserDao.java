package com.cheney.dao;

import com.cheney.entity.User;

public interface UserDao extends BaseDao<User, Long> {

    User findByUsername(String username);

}
