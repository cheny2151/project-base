package com.cheney.utils.sql;

/**
 * 装饰者模式
 * 使query只开放部分接口
 */
public class Query {

    private javax.persistence.Query query;

    public Query(javax.persistence.Query query) {
        this.query = query;
    }

    public javax.persistence.Query setParameter(String name, Object value) {
        return query.setParameter(name, value);
    }

    public javax.persistence.Query setParameter(int position, Object value) {
        return query.setParameter(position, value);
    }

}
