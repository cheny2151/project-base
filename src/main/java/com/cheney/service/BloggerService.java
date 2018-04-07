package com.cheney.service;

import com.cheney.entity.jpa.Blogger;

public interface BloggerService extends BaseService<Blogger, Long> {

    /**
     * 获取当前登陆的用户
     */
    Blogger getCurrent();

}
