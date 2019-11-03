package com.cheney.system.filter;

import java.util.ArrayList;

public class Filters extends ArrayList<Filter> {

    public Filters() {
    }

    public Filters(Filter filter) {
        super();
        add(filter);
    }

    public Filters addFilter(Filter filter) {
        this.add(filter);
        return this;
    }

}
