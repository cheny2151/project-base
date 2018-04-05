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

    private final Pageable<T> pageable;

    public Page(Pageable<T> pageable, List<T> content, long count) {
        pageable.setTotal(count);
        pageable.setPageTotal((int) ((count + pageable.getPageSize() - 1) / pageable.getPageSize()));
        this.pageable = pageable;
        this.content = content == null ? new ArrayList<>() : content;
    }

    public List<T> getContent() {
        return content;
    }

    public int getPageTotal() {
        return pageable.getPageTotal();
    }

    public int getCurrentPage() {
        return pageable.getPageNumber();
    }

    public long getEntityTotal() {
        return pageable.getTotal();
    }

    public Pageable getPageable() {
        return pageable;
    }


    public String getSearchProperty() {
        return pageable.getSearchProperty();
    }

    public String getSearchValue() {
        return pageable.getSearchValue();
    }

}
