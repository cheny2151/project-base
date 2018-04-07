package com.cheney.dao.mongo.impl;

import com.cheney.dao.mongo.BaseMongo;
import com.cheney.entity.mongo.MongoBaseEntity;
import com.cheney.system.page.Page;
import com.cheney.system.page.PageInfo;
import com.cheney.utils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.List;

class BaseMongoImpl<T extends MongoBaseEntity> implements BaseMongo<T> {

    @Autowired
    private MongoOperations mongo;

    private Class<T> entityType;

    @SuppressWarnings("unchecked")
    BaseMongoImpl() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        this.entityType = (Class<T>) pt.getActualTypeArguments()[0];
    }

    @Override
    public String save(T entity) {
        Assert.notNull(entity, "entity to save must not null");
        mongo.save(entity);
        return entity.getId();
    }

    @Override
    public void update(T entity) {
        Update update = new Update();
        Field[] fields = entityType.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            if (!"serialVersionUID".equals(fieldName) && field.getAnnotation(Transient.class) == null) {
                update.set(fieldName, BeanUtils.readValue(entity, fieldName));
            }
        }
        mongo.updateFirst(Query.query(Criteria.where("id").is(entity.getId())), update, entityType);
    }

    public void update(T entity, String[] properties) {
        if (properties != null && properties.length > 0) {
            Update update = new Update();
            for (String s : properties) {
                update.set(s, BeanUtils.readValue(entity, s));
            }
            mongo.updateFirst(Query.query(Criteria.where("id").is(entity.getId())), update, entityType);
        }
    }

    @Override
    public void remove(String id) {
        mongo.remove(Query.query(Criteria.where("id").is(id)));
    }

    @Override
    public long count(Criteria criteria) {
        return mongo.count(Query.query(criteria), entityType);
    }

    @Override
    public T findById(String id) {
        return mongo.findById(id, entityType);
    }

    @Override
    public List<T> findAll() {
        return mongo.findAll(entityType);
    }

    @Override
    public List<T> find(Criteria criteria) {
        return mongo.find(Query.query(criteria), entityType);
    }

    @Override
    public Page<T> findPage(Criteria criteria, PageInfo pageInfo, Sort.Order... orders) {
        Query query = Query.query(criteria);
        long count = mongo.count(query, entityType);
        if (orders != null) {
            query.with(Sort.by(orders));
        }
        query.skip(pageInfo.getStartSize()).limit(pageInfo.getPageSize());
        List<T> content = mongo.find(query, entityType);
        return new Page<>(pageInfo, content, count);
    }


}
