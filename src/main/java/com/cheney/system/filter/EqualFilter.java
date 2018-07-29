package com.cheney.system.filter;

public class EqualFilter extends Filter {

    private static final String equalSymbol = "=";

    public EqualFilter(String property, Object value) {
        super(equalSymbol, property, value);
    }

}
