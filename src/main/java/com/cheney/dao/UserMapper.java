package com.cheney.dao;

import com.cheney.entity.dto.AuthUser;
import org.springframework.stereotype.Repository;

@Repository("userMapper")
public interface UserMapper extends BaseMapper<AuthUser, Long> {

    AuthUser findByUsername(String username);

}
