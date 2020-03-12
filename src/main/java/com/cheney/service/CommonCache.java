package com.cheney.service;

import com.cheney.entity.BaseEntity;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 通用缓存
 *
 * @author cheney
 * @date 2019-11-10
 */
public interface CommonCache<T extends BaseEntity<ID>, ID extends Serializable> extends BaseService<T, ID> {

    /**
     * 通过唯一code查找缓存
     *
     * @param key 唯一code
     * @return 实体缓存
     */
    T getByCache(String key);

    /**
     * 获取复数缓存
     *
     * @param keys 缓存key集合
     * @return 缓存数据集合
     */
    List<T> getByCache(Collection<String> keys);

    /**
     * 获取缓存中所有数据
     *
     * @return 缓存中的数据
     */
    List<T> getAllByCache();

    /**
     * 缓存实体
     *
     * @param entity 实体
     */
    void cache(T entity);

    /**
     * 刷新所有缓存
     */
    void refreshAll();

}
