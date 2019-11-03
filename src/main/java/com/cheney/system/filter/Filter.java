package com.cheney.system.filter;

import com.cheney.utils.PropertyNameUtils;
import org.apache.commons.lang.StringUtils;

public class Filter {

    // 转为下划线
    private static boolean USE_UNDERLINE = true;

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
        if (USE_UNDERLINE && StringUtils.isNotEmpty(property)) {
            property = PropertyNameUtils.underline(property);
        }
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

}
