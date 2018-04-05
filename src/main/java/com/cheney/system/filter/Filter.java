package com.cheney.system.filter;

import org.apache.commons.lang.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

/**
 * 重构:多态代替条件
 */
public abstract class Filter {

    protected Object value;

    protected String property;

    Filter(String property, Object value) {
        if (StringUtils.isEmpty(property) || value == null) {
            throw new IllegalArgumentException("illegal property or value");
        }
        this.property = property;
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public abstract Predicate addRestriction(Predicate predicate, Path<?> path, CriteriaBuilder criteriaBuilder);

}
