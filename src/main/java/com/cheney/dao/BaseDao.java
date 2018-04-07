package com.cheney.dao;

import com.cheney.entity.jpa.BaseEntity;
import com.cheney.system.filter.Filter;
import com.cheney.system.order.Order;
import com.cheney.system.page.Page;
import com.cheney.system.page.Pageable;
import com.cheney.utils.sql.SqlFactory;

import javax.persistence.LockModeType;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface BaseDao<T extends BaseEntity, ID extends Serializable> {

    /**
     * 根据id查找对象
     */
    T find(ID id);

    /**
     * 查找，加锁
     */
    T find(ID id, LockModeType type);

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
     * 加锁
     */
    void lock(T entity, LockModeType type);

    /**
     * 根据实体获取id
     */
    ID getIdentifier(T entity);

    /**
     * 过滤排序查找
     */
    List<T> findList(Filter filter, Order... orders);

    /**
     * 过滤排序查找
     */
    List<T> findList(Collection<Filter> filters, Order... orders);

    /**
     * 过滤count
     */
    long count(Filter filter);

    /**
     * 分页
     */
    Page<T> findPage(Pageable pageable);

    /**
     * 分页原生sql
     *
     * @param selection   查找的内容
     * @param tableName   查找的表命
     * @param restriction 限定条件
     */
    Page<T> findPageNative(String selection, String[] tableName, String restriction, Pageable pageable, SqlFactory.ParameterHolder parameterHolder);

}
