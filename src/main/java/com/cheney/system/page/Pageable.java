package com.cheney.system.page;

import com.cheney.system.filter.Filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页信息
 * Created by cheny on 2017/9/24.
 */
public class Pageable<T> implements Serializable {

    private static final long serialVersionUID = 5705303253597757865L;

    /**
     * 默认分页大小
     */
    private final static int DEFAULT_PAGE_SIZE = 20;

    private final static int DEFAULT_PAGE_NUMBER = 1;

    private int pageNumber = DEFAULT_PAGE_NUMBER;

    private int pageSize = DEFAULT_PAGE_SIZE;

    private int pageTotal;

    private long total;

    private String searchProperty;

    private String searchValue;

    private List<Filter> filter = new ArrayList<>();

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageTotal() {
        return pageTotal;
    }

    public void setPageTotal(int pageTotal) {
        this.pageTotal = pageTotal;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getSearchProperty() {
        return searchProperty;
    }

    public void setSearchProperty(String searchProperty) {
        this.searchProperty = searchProperty;
    }

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public List<Filter> getFilter() {
        return filter;
    }

    public void setFilter(List<Filter> filter) {
        this.filter = filter;
    }

    /**
     * 分页起始位置
     */
    public int getStartSize() {
        return (pageNumber - 1) * pageSize;
    }

    @Override
    public String toString() {
        return "Pageable{" +
                "pageNumber=" + pageNumber +
                ", pageSize=" + pageSize +
                ", pageTotal=" + pageTotal +
                ", total=" + total +
                ", searchProperty='" + searchProperty + '\'' +
                ", searchValue='" + searchValue + '\'' +
                ", filter=" + filter +
                '}';
    }
}
