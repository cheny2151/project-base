package com.cheney.service.impl;

import com.cheney.dao.mybatis.BaseMapper;
import com.cheney.entity.BaseEntity;
import com.cheney.redis.client.impl.JsonRedisClient;
import com.cheney.service.BaseService;
import com.cheney.service.CommonCache;
import com.cheney.utils.ReflectUtils;
import com.cheney.utils.annotation.CacheKey;

import java.io.Serializable;

/**
 * 通用缓存实现类
 *
 * @author cheney
 * @date 2019-11-10
 */
public class CommonCacheImpl<T extends BaseEntity<ID>, ID extends Serializable> extends BaseServiceImpl<T, ID>
        implements BaseService<T, ID>, CommonCache<T, ID> {

    private JsonRedisClient<T> jsonRedisClient;

    protected CommonCacheImpl(BaseMapper<T, ID> baseMapper, JsonRedisClient<T> jsonRedisClient) {
        super(baseMapper);
        this.jsonRedisClient = jsonRedisClient;
    }

    @Override
    public BaseEntity getByCode(Object key) {
        return null;
    }

    @Override
    public void cache(BaseEntity baseEntity) {
        CacheKey cacheKey = baseEntity.getClass().getDeclaredAnnotation(CacheKey.class);
        String property = cacheKey.key();
        Object key = ReflectUtils.readValue(baseEntity, property);
        System.out.println(key);
    }

    @Override
    public void refreshAll() {

    }
}
