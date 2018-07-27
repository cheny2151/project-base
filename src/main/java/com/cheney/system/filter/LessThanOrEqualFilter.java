package com.cheney.system.filter;

public class LessThanOrEqualFilter extends Filter {

    private static final String lessThanOrEqualSymbol = "<=";

    public LessThanOrEqualFilter() {
        super(lessThanOrEqualSymbol);
    }

    public LessThanOrEqualFilter(Object value, String property) {
        super(lessThanOrEqualSymbol, value, property);
    }

}
