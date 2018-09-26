package com.cheney.system.filter;

import java.util.List;

public class InFilter extends Filter {

    private static final String inSymbol = "in";

    public <T> InFilter(String property, List<T> value) {
        super(inSymbol, property, value);
    }

}
