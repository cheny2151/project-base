package com.cheney.system.filter;

public class NotNullFilter extends Filter {

    private static final String noNullSymbol = "is not null";

    public NotNullFilter(String property) {
        super(noNullSymbol, property, null);
    }
    
}
