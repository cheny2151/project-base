package com.cheney.system.filter;

public class GreaterThanOrEqualFilter extends Filter {

    private static final String greaterThanOrEqualSymbol = ">=";

    public GreaterThanOrEqualFilter() {
        super(greaterThanOrEqualSymbol);
    }

    public GreaterThanOrEqualFilter(Object value, String property) {
        super(greaterThanOrEqualSymbol, value, property);
    }

}
