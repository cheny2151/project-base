package com.cheney.system.page;

import com.cheney.system.filter.Filter;
import com.cheney.system.filter.Filters;
import com.cheney.system.order.Orders;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Map;

/**
 * 分页信息(带过滤,排序)
 * Created by cheny on 2017/9/24.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Pageable extends PageInfo {

    private static final long serialVersionUID = 5705303253597757865L;

    private Filters filters;

    private Orders orders;

    public Pageable() {
    }

    public Pageable(int pageNumber, int pageSize) {
        super(pageNumber,pageSize);
    }

    public static Pageable createByPageInfo(PageInfo pageInfo) {
        return new Pageable(pageInfo.getPageNumber(), pageInfo.getPageSize());
    }

    /**
     * 为Filters添加Filter
     *
     * @param filter 过滤条件
     * @return Filters
     */
    public Filters addFilter(Filter filter) {
        Filters filters = this.filters;
        if (filters == null) {
            filters = this.filters = Filters.build();
        }
        filters.andFilter(filter);
        return filters;
    }

    /**
     * 添加其他过滤条件到Filters对象
     *
     * @param params 其他过滤条件
     * @return Filters
     */
    public Filters addOtherParams(Map<String, Object> params) {
        Filters filters = this.filters;
        if (filters == null) {
            filters = this.filters = new Filters();
        }
        filters.addOtherParams(params);
        return filters;
    }

}
