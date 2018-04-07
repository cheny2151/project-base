package com.cheney.service.impl;

import com.cheney.dao.BaseDao;
import com.cheney.entity.jpa.BaseEntity;
import com.cheney.service.BaseService;
import com.cheney.system.filter.Filter;
import com.cheney.system.page.Page;
import com.cheney.system.page.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@Transactional
public class BaseServiceImpl<T extends BaseEntity, ID extends Serializable> implements BaseService<T, ID> {

    private BaseDao<T, ID> baseDao;

    protected void setBaseDao(BaseDao<T, ID> baseDao) {
        this.baseDao = baseDao;
    }

    @Override
    public T find(ID id) {
        return baseDao.find(id);
    }

    @Override
    public List<T> findAll() {
        return baseDao.findAll();
    }

    @Override
    public void save(T entity) {
        baseDao.persist(entity);
    }

    @Override
    public void update(T entity) {
        baseDao.merge(entity);
    }

    @Override
    public void delete(ID id) {
        baseDao.remove(id);
    }

    @Override
    public void delete(T entity) {
        baseDao.remove(entity);
    }

    @Override
    public void delete(ID[] ids) {
        baseDao.remove(ids);
    }

    @Override
    public void flush() {
        baseDao.flush();
    }

    @Override
    public List<T> findList(Filter filter) {
        return baseDao.findList(filter);
    }

    @Override
    public List<T> findList(Collection<Filter> filters) {
        return baseDao.findList(filters);
    }

    @Override
    public long count(Filter filter) {
        return baseDao.count(filter);
    }

    @Override
    public Page<T> findPage(Pageable pageable) {
        return baseDao.findPage(pageable);
    }

}
