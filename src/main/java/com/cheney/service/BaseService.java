package com.cheney.service;

import cn.cheny.toolbox.other.filter.Filters;
import cn.cheny.toolbox.other.order.Orders;
import cn.cheny.toolbox.other.page.Page;
import cn.cheny.toolbox.other.page.Pageable;
import com.cheney.entity.BaseEntity;

import java.io.Serializable;
import java.util.List;

/**
 * service基类
 *
 * @param <T>  entity
 * @param <ID> id
 */
public interface BaseService<T extends BaseEntity<ID>, ID extends Serializable> {

    /**
     * 根据id查找对象
     */
    T find(ID id);

    /**
     * 根据filter查找
     */
    T filter(Filters filters);

    /**
     * 查找所有实体
     */
    List<T> findAll();

    /**
     * 保存
     */
    int save(T entity);

    /**
     * 更新
     */
    int update(T entity);

    /**
     * 删除
     */
    int delete(ID id);

    /**
     * 批量删除
     */
    int delete(ID[] ids);

    /**
     * 过滤
     */
    List<T> findList(Filters filters);

    /**
     * 过滤
     */
    List<T> findList(Filters filters, Orders orders);

    /**
     * 过滤count
     */
    long count(Filters filters);

    /**
     * 分页
     */
    Page<T> findPage(Pageable pageable);

}
