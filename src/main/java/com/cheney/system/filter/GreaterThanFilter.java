package com.cheney.system.filter;

public class GreaterThanFilter extends Filter {

    private static final String greaterThanSymbol = ">";

    public GreaterThanFilter() {
        super(greaterThanSymbol);
    }

    public GreaterThanFilter(String property, Object value) {
        super(greaterThanSymbol, property, value);
    }

}
