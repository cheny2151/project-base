package com.cheney.service.impl;

import cn.cheny.toolbox.other.order.Orders;
import cn.cheny.toolbox.other.page.Page;
import cn.cheny.toolbox.other.page.Pageable;
import com.cheney.dao.mybatis.BaseMapper;
import com.cheney.entity.BaseEntity;
import com.cheney.service.BaseService;
import cn.cheny.toolbox.other.filter.Filter;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Transactional
public class BaseServiceImpl<T extends BaseEntity<ID>, ID extends Serializable> implements BaseService<T, ID> {

    private BaseMapper<T, ID> baseMapper;

    protected BaseServiceImpl(BaseMapper<T, ID> baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public T find(ID id) {
        return baseMapper.find(id);
    }

    @Override
    public T filter(List<Filter> filters) {
        return baseMapper.filter(filters);
    }

    @Override
    public List<T> findAll() {
        return baseMapper.findAll();
    }

    @Override
    public int save(T entity) {
        return baseMapper.persist(entity);
    }

    @Override
    public int update(T entity) {
        return baseMapper.merge(entity);
    }

    @Override
    public int delete(ID id) {
        return baseMapper.remove(id);
    }

    @Override
    public int delete(ID[] ids) {
        return baseMapper.multiRemove(ids);
    }

    @Override
    public List<T> findList(Filter filter) {
        List<Filter> filters = new ArrayList<>();
        filters.add(filter);
        return baseMapper.findList(filters, null);
    }

    @Override
    public List<T> findList(Filter filter, Orders orders) {
        List<Filter> filters = new ArrayList<>();
        filters.add(filter);
        return baseMapper.findList(filters, orders);
    }

    @Override
    public List<T> findList(Collection<Filter> filters) {
        return baseMapper.findList(filters, null);
    }

    @Override
    public List<T> findList(Collection<Filter> filters, Orders orders) {
        return baseMapper.findList(filters, orders);
    }

    @Override
    public long count(Collection<Filter> filters) {
        return baseMapper.count(filters);
    }

    @Override
    public Page<T> findPage(Pageable pageable) {
        long total = baseMapper.count(pageable.getFilters());
        if (total == 0) {
            return Page.emptyPage(pageable);
        }
        List<T> content = baseMapper.findPage(pageable);
        return new Page<>(content, total, pageable);
    }


}
