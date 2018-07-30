package com.cheney.system.order;

import org.apache.commons.lang.StringUtils;

/**
 * 排序父类
 */
public abstract class Order {

    private String property;

    private String type;

    public Order(String property, String type) {
        if (StringUtils.isEmpty(property)) {
            throw new IllegalArgumentException("illegal arg property");
        }
        this.property = property;
        this.type = type;
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
