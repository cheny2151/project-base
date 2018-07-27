package com.cheney.system.filter;

public class LessThanFilter extends Filter {

    private static final String lessThanSymbol = "=";

    public LessThanFilter() {
        super(lessThanSymbol);
    }

    public LessThanFilter(Object value, String property) {
        super(lessThanSymbol, value, property);
    }

}
