package com.cheney.system.filter;

public class InFilter extends Filter {

    private static final String inSymbol = "=";

    public InFilter() {
        super(inSymbol);
    }

    public InFilter(Object value, String property) {
        super(inSymbol, value, property);
    }

}
