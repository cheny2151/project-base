package com.cheney.service;

import com.cheney.entity.BaseEntity;

import java.io.Serializable;

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
    T getByCache(Object key);

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
