package com.cheney.system.page;

import com.cheney.system.filter.Filter;
import com.cheney.system.order.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页信息(带过滤,排序)
 * Created by cheny on 2017/9/24.
 */
public class Pageable extends PageInfo {

    private static final long serialVersionUID = 5705303253597757865L;

    private List<Filter> filters = new ArrayList<>();

    private List<Order> orders = new ArrayList<>();

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

}
