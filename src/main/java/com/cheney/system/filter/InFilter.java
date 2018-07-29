package com.cheney.system.filter;

public class InFilter extends Filter {

    private static final String inSymbol = "in";

    public InFilter(String property, Object value) {
        super(inSymbol, property, value);
    }

}
