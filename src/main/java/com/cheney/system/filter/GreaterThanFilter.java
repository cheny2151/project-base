package com.cheney.system.filter;

public class GreaterThanFilter extends Filter {

    private static final String greaterThanSymbol = "=";

    public GreaterThanFilter() {
        super(greaterThanSymbol);
    }

    public GreaterThanFilter(Object value, String property) {
        super(greaterThanSymbol, value, property);
    }

}
