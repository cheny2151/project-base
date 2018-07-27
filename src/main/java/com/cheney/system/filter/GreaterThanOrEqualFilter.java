package com.cheney.system.filter;

public class GreaterThanOrEqualFilter extends Filter {

    private static final String greaterThanOrEqualSymbol = ">=";

    public GreaterThanOrEqualFilter() {
        super(greaterThanOrEqualSymbol);
    }

    public GreaterThanOrEqualFilter(String property, Object value) {
        super(greaterThanOrEqualSymbol, property, value);
    }

}
