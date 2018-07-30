package com.cheney.dao.mongo.impl;

import com.cheney.dao.mongo.BaseMongo;
import com.cheney.entity.mongo.MongoBaseEntity;
import com.cheney.system.page.Page;
import com.cheney.system.page.PageInfo;
import com.cheney.utils.BeanUtils;
import com.cheney.utils.MongoEntityHelp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import java.lang.reflect.ParameterizedType;
import java.util.Date;
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
        entity.setCreateDate(new Date());
        mongo.save(entity);
        return entity.getId();
    }

    @Override
    public void update(T entity) {
        Assert.notNull(entity.getId(), " 更新实体id不能为null");
        Update update = MongoEntityHelp.update(entity);
        System.out.println(update.toString());
        mongo.updateFirst(Query.query(Criteria.where("id").is(entity.getId())), update, entityType);
    }

    @Override
    public void update(T entity, String[] properties) {
        Assert.notNull(entity.getId(), " 更新实体id不能为null");
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
        Query query = criteria == null ? new Query() : Query.query(criteria);
        return mongo.count(query, entityType);
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
        return criteria == null ? mongo.findAll(entityType) : mongo.find(Query.query(criteria), entityType);
    }

    @Override
    public Page<T> findPage(Criteria criteria, PageInfo pageInfo, Sort.Order... orders) {
        Query query = criteria == null ? new Query() : new Query(criteria);
        long count = mongo.count(query, entityType);
        if (orders != null) {
            query.with(Sort.by(orders));
        }
        query.skip(pageInfo.getStartSize()).limit(pageInfo.getPageSize());
        List<T> content = mongo.find(query, entityType);
        return new Page<>(content, count, pageInfo);
    }

    //rouji mongo聚合代码
    /*public void aggregation(){
        //方式一：
        AggregationResults<BasicDBObject> aggregate = mongoTemplate.aggregate(
                Aggregation.newAggregation(
                        HuanXinMessage.class,
                        Aggregation.match(Criteria.where("receiver").is("d" + doctor.getUsername()).and("unread").is(true)),
                        Aggregation.group("sender").count().as("count")
                ),
                BasicDBObject.class
        );
        List<BasicDBObject> mappedResults = aggregate.getMappedResults();

        HashMap<Long, Integer> result = new HashMap<>();

        for (BasicDBObject object : mappedResults) {
            result.put(Long.valueOf(object.getString("_id").substring(1)), object.getInt("count"));
        }

        //方式二：
        HashMap<Long, Integer> result = new HashMap<>();
        ArrayList<DBObject> dbObjects = new ArrayList<>();
        dbObjects.add(new BasicDBObject("$match", new BasicDBObject("receiver", "d" + doctor.getUsername())));
        dbObjects.add(new BasicDBObject("$group", new BasicDBObject("_id", "$sender").append("count", new BasicDBObject("$sum", 1))));
        AggregationOutput output = mongoTemplate.getCollection(mongoTemplate.getCollectionName(HuanXinMessage.class)).aggregate(dbObjects);
        for (DBObject dbObject : output.results()) {
            result.put(Long.valueOf(dbObject.get("_id").toString().substring(1)), Integer.valueOf(dbObject.get("count").toString()));
        }
    }*/


}
