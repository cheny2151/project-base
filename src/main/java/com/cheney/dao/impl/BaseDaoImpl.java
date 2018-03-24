package com.cheney.dao.impl;

import com.cheney.dao.BaseDao;
import com.cheney.entity.BaseEntity;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;


/**
 * dao基类
 *
 * @param <T>  entity
 * @param <ID> id
 */
public class BaseDaoImpl<T extends BaseEntity, ID extends Serializable> implements BaseDao<T, ID> {

    @PersistenceContext
    protected EntityManager entityManager;

    private Class<T> entityType;

    @SuppressWarnings("unchecked")
    public BaseDaoImpl() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.entityType = (Class<T>) pt.getActualTypeArguments()[0];
    }

    @Override
    public T find(ID id) {
        return id == null ? null : entityManager.find(entityType, id);
    }

    @Override
    public List<T> findAll() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = cb.createQuery(entityType);
        Root<T> root = query.from(entityType);
        query.select(root);
        return entityManager.createQuery(query).setFlushMode(FlushModeType.COMMIT).getResultList();
    }

    @Override
    public void persist(T entity) {
        Assert.notNull(entity, "not null entity");
        entityManager.persist(entity);
    }

    @Override
    public void merge(T entity) {
        Assert.notNull(entity, "not null entity");
        entityManager.merge(entity);
    }

    @Override
    public void remove(ID ID) {
        T entity;
        if ((entity = find(ID)) != null) {
            System.out.println(entity.getId());
            try {
                entityManager.remove(entity);
                System.out.println("------------");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void remove(T entity) {
        Assert.notNull(entity, "not null entity");
        entityManager.remove(entity);
    }

    @Override
    public void remove(ID[] ids) {
        if (ids == null || ids.length == 0) {
            return;
        }
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaDelete<T> delete = cb.createCriteriaDelete(entityType);
        Root<T> root = delete.from(entityType);
        delete.where(root.get("id").in(Arrays.asList(ids)));
        entityManager.createQuery(delete).setFlushMode(FlushModeType.COMMIT).executeUpdate();
    }

    @Override
    public void flush() {
        entityManager.flush();
    }

    @SuppressWarnings("unchecked")
    @Override
    public ID getIdentifier(T entity) {
        Assert.notNull(entity, "not null entity");
        return (ID) entityManager.getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
    }

}
