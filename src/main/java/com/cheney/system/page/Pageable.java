package com.cheney.system.page;

import com.cheney.system.filter.Filter;
import com.cheney.system.order.Orders;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * 分页信息(带过滤,排序)
 * Created by cheny on 2017/9/24.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Pageable extends PageInfo {

    private static final long serialVersionUID = 5705303253597757865L;

    private List<Filter> filters;

    private Orders orders;

    public Pageable() {
    }

    public Pageable(int pageNumber, int pageSize) {
        super(pageNumber,pageSize);
    }

    public static Pageable createByPageInfo(PageInfo pageInfo) {
        return new Pageable(pageInfo.getPageNumber(), pageInfo.getPageSize());
    }

    public void addFilters(List<Filter> filters) {
        if (this.filters == null) {
            this.filters = new ArrayList<>();
        }
        this.filters.addAll(filters);
    }

}
