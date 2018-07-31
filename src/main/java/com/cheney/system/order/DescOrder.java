package com.cheney.system.order;

/**
 * 降序
 */
public class DescOrder extends Order {

    private final static String TYPE = "desc";

    public DescOrder(String... property) {
        super(property, TYPE);
    }

}
