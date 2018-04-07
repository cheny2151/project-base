package com.cheney.system.page;

import java.io.Serializable;

/**
 * 基本分页信息
 */
public class PageInfo implements Serializable {

    private static final long serialVersionUID = 7439257553404129509L;

    /**
     * 默认分页大小
     */
    private final static int DEFAULT_PAGE_SIZE = 20;

    /**
     * 默认页码
     */
    private final static int DEFAULT_PAGE_NUMBER = 1;

    private int pageNumber = DEFAULT_PAGE_NUMBER;

    private int pageSize = DEFAULT_PAGE_SIZE;

    private int pageTotal;

    private long total;


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

    /**
     * 分页起始位置
     */
    public int getStartSize() {
        return (pageNumber - 1) * pageSize;
    }

}
