package com.cheney.system.filter;

public class NotLikeFilter extends Filter {

    private static final String notLikeSymbol = "no like";

    public NotLikeFilter(String property, Object value) {
        super(notLikeSymbol, property, value);
    }

}