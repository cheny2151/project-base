package com.cheney.service.impl;

import cn.cheny.toolbox.other.filter.Filters;
import cn.cheny.toolbox.redis.client.impl.JsonRedisClient;
import com.cheney.constants.CacheBaseKey;
import com.cheney.dao.mybatis.RoleMapper;
import com.cheney.entity.Role;
import com.cheney.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * Role - serviceImpl
 */
@Service("roleServiceImpl")
@Transactional
public class RoleServiceImpl extends CommonCacheImpl<Role, Long> implements RoleService {

    @Resource
    private RoleMapper roleMapper;

    @Autowired
    public RoleServiceImpl(RoleMapper roleMapper, JsonRedisClient<Role> jsonRedisClient) {
        super(roleMapper, jsonRedisClient, CacheBaseKey.ROLE_KEY.getKey());
    }

    @Override
    public List<Role> findByCacheKeys(Collection<String> keys) {
        Filters filters = Filters.create(Filters.in("code", keys));
        return roleMapper.findList(filters, null);
    }
}
