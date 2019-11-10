package com.cheney.service.impl;

import com.cheney.dao.mybatis.RoleMapper;
import com.cheney.entity.Role;
import com.cheney.javaconfig.redis.CacheBaseKey;
import com.cheney.redis.client.impl.JsonRedisClient;
import com.cheney.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * Role - serviceImpl
 */
@Service("roleServiceImpl")
@Transactional
public class RoleServiceImpl extends CommonCacheImpl<Role, Long> implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Autowired
    public RoleServiceImpl(RoleMapper roleMapper, JsonRedisClient<Role> redisClient) {
        super(roleMapper, redisClient, CacheBaseKey.ROLE_KEY.getKey());
    }

}
