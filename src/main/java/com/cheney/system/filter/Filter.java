package com.cheney.system.filter;

public class Filter {

    private String symbol;

    private Object value;

    private String property;

    public Filter(String symbol) {
        this.symbol = symbol;
    }

    public Filter(String symbol, String property, Object value) {
        this.symbol = symbol;
        this.property = property;
        this.value = value;
    }

    public String getSymbol() {
        return symbol;
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

}
