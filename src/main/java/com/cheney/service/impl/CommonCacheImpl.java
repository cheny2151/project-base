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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用缓存实现类
 *
 * @author cheney
 * @date 2019-11-10
 */
public class CommonCacheImpl<T extends BaseEntity<ID>, ID extends Serializable> extends BaseServiceImpl<T, ID>
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
        return jsonRedisClient.HGetForMap(baseKey, key);
    }

    @Override
    public List<T> getByCache(Collection<String> keys) {
        return jsonRedisClient.HMGet(baseKey, keys);
    }

    @Override
    public List<T> getAllByCache() {
        return jsonRedisClient.getList(baseKey);
    }

    @Override
    public void cache(T entity) {
        CacheKey cacheKey = entity.getClass().getDeclaredAnnotation(CacheKey.class);
        String property = cacheKey.key();
        Object key = ReflectUtils.readValue(entity, property);
        jsonRedisClient.HSetForMap(baseKey, String.valueOf(key), entity);
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
        jsonRedisClient.HMSetForMap(baseKey, allMap);
    }

}
