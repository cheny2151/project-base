package com.cheney.dao;

import com.cheney.entity.jpa.Blogger;

public interface BloggerDao extends BaseDao<Blogger, Long> {
    /**
     * 通过username查找用户
     */
    Blogger findByUsername(String username);
}
