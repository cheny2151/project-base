package com.cheney.system.page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页最外层(页面展示用)
 * Created by cheny on 2017/9/24.
 */
public class Page<T> implements Serializable {

    private static final long serialVersionUID = 285590709870311132L;

    private List<T> content;

    private final PageInfo pageInfo;

    public Page(PageInfo pageInfo, List<T> content, long count) {
        pageInfo.setTotal(count);
        pageInfo.setPageTotal((int) ((count + pageInfo.getPageSize() - 1) / pageInfo.getPageSize()));
        this.pageInfo = pageInfo;
        this.content = content == null ? new ArrayList<>() : content;
    }

    public List<T> getContent() {
        return content;
    }

    public int getPageTotal() {
        return pageInfo.getPageTotal();
    }

    public int getCurrentPage() {
        return pageInfo.getPageNumber();
    }

    public long getTotal() {
        return pageInfo.getTotal();
    }

    public PageInfo getPageable() {
        return pageInfo;
    }

}
