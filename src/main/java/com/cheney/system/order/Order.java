package com.cheney.system.order;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;

/**
 * 排序父类
 */
public abstract class Order {

    protected String property;

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    abstract javax.persistence.criteria.Order create(Root<?> root, CriteriaBuilder criteriaBuilder);

}
