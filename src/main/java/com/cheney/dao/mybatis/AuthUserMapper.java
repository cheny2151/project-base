package com.cheney.dao.mybatis;

import com.cheney.entity.AuthUser;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

public interface AuthUserMapper extends BaseMapper<AuthUser, Long> {

    /**
     * 根据用户名查找用户
     *
     * @param username 用户名
     * @return 用户
     */
    AuthUser findByUsername(String username);

    List<AuthUser> test(@Param("date1") Object date1, @Param("date2") Object date2);

}
