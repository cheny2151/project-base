package com.cheney.system.filter;

public class LikeFilter extends Filter {

    private static final String likeSymbol = "like";

    public LikeFilter() {
        super(likeSymbol);
    }

    public LikeFilter(Object value, String property) {
        super(likeSymbol, value, property);
    }
}
