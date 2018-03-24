package com.cheney.system.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页信息
 * Created by cheny on 2017/9/24.
 */
public class Pageable<T> implements Serializable{

    private static final long serialVersionUID = 5705303253597757865L;

    /**
     * 默认分页大小
     */
    private final static int DEFAULT_PAGE_SIZE = 20;

    private final static int DEFAULT_CURRENT_PAGE = 1;

    private int currentPage = DEFAULT_CURRENT_PAGE;

    private int pageSize = DEFAULT_PAGE_SIZE;

    private int pageTotal;

    private int entityTotal;

    private List<T> content = new ArrayList<>();

    private String searchProperty;

    private String searchValue;

    private List<Filter> filter = new ArrayList<>();

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
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

    public int getEntityTotal() {
        return entityTotal;
    }

    public void setEntityTotal(int entityTotal) {
        this.entityTotal = entityTotal;
    }

    public List<T> getContent() {
        return content;
    }

    public void setContent(List<T> content) {
        this.content = content;
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

}
