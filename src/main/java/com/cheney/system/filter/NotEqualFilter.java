package com.cheney.system.filter;

public class NotEqualFilter extends Filter {

    private static final String noEqualSymbol = "!=";

    public NotEqualFilter() {
        super(noEqualSymbol);
    }

    public NotEqualFilter(String property, Object value) {
        super(noEqualSymbol, property, value);
    }

}
