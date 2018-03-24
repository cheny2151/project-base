package com.cheney.dao;

import com.cheney.entity.BaseEntity;

import java.io.Serializable;
import java.util.List;

public interface BaseDao<T extends BaseEntity, ID extends Serializable> {

    /**
     * 根据id查找对象
     */
    T find(ID id);

    /**
     * 查找所有实体
     */
    List<T> findAll();

    /**
     * 保存
     */
    void persist(T entity);

    /**
     * 更新
     */
    void merge(T entity);

    /**
     * 删除
     */
    void remove(ID id);

    /**
     * 删除
     */
    void remove(T entity);

    /**
     * 批量删除
     */
    void remove(ID[] ids);

    /**
     * flush
     */
    void flush();

    /**
     * 根据实体获取id
     */
    ID getIdentifier(T entity);

}
