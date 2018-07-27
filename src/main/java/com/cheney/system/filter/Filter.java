package com.cheney.system.filter;

public abstract class Filter {

    private String symbol;

    private Object value;

    private String property;

    public Filter(String symbol) {
        this.symbol = symbol;
    }

    public Filter(String symbol, Object value, String property) {
        this.symbol = symbol;
        this.value = value;
        this.property = property;
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
