package com.cheney.dao.mybatis;

import com.cheney.entity.BaseEntity;
import com.cheney.system.filter.Filter;
import com.cheney.system.filter.Filters;
import com.cheney.system.order.Orders;
import com.cheney.system.page.Page;
import com.cheney.system.page.Pageable;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

public interface BaseMapper<T extends BaseEntity, ID extends Serializable> {

    /**
     * 根据id查找对象
     */
    T find(ID id);

    /**
     * 根据filter查找
     */
    T filter(@Param("filters") List<Filter> filters);

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
     * 批量删除
     */
    void multiRemove(@Param("ids") ID[] ids);

    /**
     * 过滤排序查找
     */
    List<T> findList(@Param("filters") Collection<Filter> filters, @Param("orders") Orders orders);

    /**
     * 过滤count
     */
    long count(@Param("filters") Collection<Filter> filters);

    /**
     * 判断是否存在
     */
    boolean exists(@Param("filters") Collection<Filter> filters);

    /**
     * 分页
     * @return maybe null
     */
    List<T> findPage(@Param("pageable") Pageable pageable);

}
