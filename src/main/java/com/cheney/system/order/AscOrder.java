package com.cheney.system.order;

/**
 * 升序
 */
public class AscOrder extends Order {

    private final static String TYPE = "asc";

    public AscOrder(String property) {
        super(property, TYPE);
    }

}
