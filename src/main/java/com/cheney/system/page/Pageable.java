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

    private Order order;

    public Pageable() {
    }

    public Pageable(int pageNumber, int pageSize) {
        super(pageNumber,pageSize);
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public static Pageable createByPageInfo(PageInfo pageInfo) {
        return new Pageable(pageInfo.getPageNumber(), pageInfo.getPageSize());
    }

    public void addFilters(List<Filter> filters) {
        this.filters.addAll(filters);
    }

}
