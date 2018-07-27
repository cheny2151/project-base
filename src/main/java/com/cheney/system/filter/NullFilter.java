package com.cheney.system.filter;

public class NullFilter extends Filter {

    private static final String nullSymbol = "is null";

    public NullFilter() {
        super(nullSymbol);
    }

}
