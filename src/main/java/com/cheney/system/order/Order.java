package com.cheney.system.order;

/**
 * 排序父类
 */
public abstract class Order {

    private String[] properties;

    private String type;

    public Order(String[] properties, String type) {
        if (properties == null || properties.length == 0) {
            throw new IllegalArgumentException("illegal arg property");
        }
        this.properties = properties;
        this.type = type;
    }

    public String[] getProperties() {
        return properties;
    }

    public void setProperties(String[] properties) {
        this.properties = properties;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
