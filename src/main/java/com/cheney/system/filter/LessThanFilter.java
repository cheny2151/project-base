package com.cheney.system.filter;

public class LessThanFilter extends Filter {

    private static final String lessThanSymbol = "<";

    public LessThanFilter() {
        super(lessThanSymbol);
    }

    public LessThanFilter(String property, Object value) {
        super(lessThanSymbol, property, value);
    }

}
