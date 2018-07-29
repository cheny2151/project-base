package com.cheney.system.filter;

public class NullFilter extends Filter {

    private static final String nullSymbol = "is null";

    public NullFilter(String property) {
        super(nullSymbol, property, null);
    }

}
