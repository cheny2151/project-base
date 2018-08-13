package com.cheney.service;

import com.cheney.entity.dto.BaseEntity;
import com.cheney.system.filter.Filter;
import com.cheney.system.order.Order;
import com.cheney.system.page.Page;
import com.cheney.system.page.Pageable;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * service基类
 *
 * @param <T>  entity
 * @param <ID> id
 */
public interface BaseService<T extends BaseEntity, ID extends Serializable> {

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
    void save(T entity);

    /**
     * 更新
     */
    void update(T entity);

    /**
     * 删除
     */
    void delete(ID id);

    /**
     * 批量删除
     */
    void delete(ID[] ids);

    /**
     * 过滤
     */
    List<T> findList(Filter filter);

    /**
     * 过滤
     */
    List<T> findList(Filter filter, Order order);

    /**
     * 过滤
     */
    List<T> findList(Collection<Filter> filters);

    /**
     * 过滤
     */
    List<T> findList(Collection<Filter> filters, Order order);

    /**
     * 过滤count
     */
    long count(Filter filter);

    /**
     * 分页
     */
    Page<T> findPage(Pageable pageable);

}
