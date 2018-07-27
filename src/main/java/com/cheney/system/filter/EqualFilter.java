package com.cheney.system.filter;

public class EqualFilter extends Filter {

    private static final String equalSymbol = "=";

    public EqualFilter() {
        super(equalSymbol);
    }

    public EqualFilter(Object value, String property) {
        super(equalSymbol, value, property);
    }

}
