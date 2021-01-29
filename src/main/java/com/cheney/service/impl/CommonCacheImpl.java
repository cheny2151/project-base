package com.cheney.service.impl;

import cn.cheny.toolbox.redis.client.impl.JsonRedisClient;
import com.cheney.dao.mybatis.BaseMapper;
import com.cheney.entity.BaseEntity;
import com.cheney.service.BaseService;
import com.cheney.service.CommonCache;
import com.cheney.utils.ReflectUtils;
import com.cheney.utils.annotation.CacheKey;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.*;

/**
 * 通用缓存实现类
 *
 * @author cheney
 * @date 2019-11-10
 */
public abstract class CommonCacheImpl<T extends BaseEntity<ID>, ID extends Serializable> extends BaseServiceImpl<T, ID>
        implements BaseService<T, ID>, CommonCache<T, ID> {

    private JsonRedisClient<T> jsonRedisClient;

    private String baseKey;

    protected CommonCacheImpl(BaseMapper<T, ID> baseMapper, JsonRedisClient<T> jsonRedisClient, String baseKey) {
        super(baseMapper);
        this.jsonRedisClient = jsonRedisClient;
        this.baseKey = baseKey;
    }

    @Override
    public T getByCache(String key) {
        T entity = jsonRedisClient.hGet(baseKey, key);
        if (entity == null) {
            List<T> entities = findByCacheKeys(Collections.singletonList(key));
            if (!CollectionUtils.isEmpty(entities)) {
                entity = entities.get(0);
                cache(entity);
            }
        }
        return entity;
    }

    @Override
    public List<T> getByCache(Collection<String> keys) {
        List<T> entities = jsonRedisClient.hValues(baseKey, keys);
        if (CollectionUtils.isEmpty(entities) || entities.size() != keys.size()) {
            entities = findByCacheKeys(keys);
            if (!CollectionUtils.isEmpty(entities)) {
                entities.forEach(this::cache);
            }
        }
        return entities;
    }

    public abstract List<T> findByCacheKeys(Collection<String> keys);

    @Override
    public List<T> getAllByCache() {
        return jsonRedisClient.getList(baseKey);
    }

    @Override
    public void cache(T entity) {
        CacheKey cacheKey = entity.getClass().getDeclaredAnnotation(CacheKey.class);
        String property = cacheKey.key();
        Object key = ReflectUtils.readValue(entity, property);
        jsonRedisClient.hSet(baseKey, String.valueOf(key), entity);
    }

    @Override
    public void refreshAll() {
        List<T> all = super.findAll();
        if (CollectionUtils.isEmpty(all)) {
            return;
        }
        CacheKey cacheKey = all.get(0).getClass().getDeclaredAnnotation(CacheKey.class);
        String property = cacheKey.key();
        Map<String, T> allMap = new HashMap<>();
        all.forEach(e -> {
            Object key = ReflectUtils.readValue(e, property);
            allMap.put(String.valueOf(key), e);
        });
        jsonRedisClient.hSetMap(baseKey, allMap);
    }

}
