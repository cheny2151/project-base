package com.cheney.service.impl;

import com.cheney.dao.mybatis.BaseMapper;
import com.cheney.entity.dto.BaseEntity;
import com.cheney.service.BaseService;
import com.cheney.system.filter.Filter;
import com.cheney.system.order.Order;
import com.cheney.system.page.Page;
import com.cheney.system.page.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Transactional
public class BaseServiceImpl<T extends BaseEntity, ID extends Serializable> implements BaseService<T, ID> {

    private BaseMapper<T, ID> baseMapper;

    protected void setBaseMapper(BaseMapper<T, ID> baseMapper) {
        this.baseMapper = baseMapper;
    }

    @Override
    public T find(ID id) {
        return baseMapper.find(id);
    }

    @Override
    public List<T> findAll() {
        return baseMapper.findAll();
    }

    @Override
    public void save(T entity) {
        baseMapper.persist(entity);
    }

    @Override
    public void update(T entity) {
        baseMapper.merge(entity);
    }

    @Override
    public void delete(ID id) {
        baseMapper.remove(id);
    }

    @Override
    public void delete(ID[] ids) {
        baseMapper.multiRemove(ids);
    }

    @Override
    public List<T> findList(Filter filter, Order order) {
        List<Filter> filters = new ArrayList<>();
        filters.add(filter);
        return baseMapper.findList(filters, order);
    }

    @Override
    public List<T> findList(Collection<Filter> filters) {
        return baseMapper.findList(filters, null);
    }

    @Override
    public long count(Filter filter) {
        return 0;
    }

    @Override
    public Page<T> findPage(Pageable pageable) {
        return baseMapper.findPage(pageable);
    }


}
