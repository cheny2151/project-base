package com.cheney.system.page;

import java.io.Serializable;
import java.util.List;

/**
 * 分页最外层(页面展示用)
 * Created by cheny on 2017/9/24.
 */
public class Page<T> implements Serializable{

    private static final long serialVersionUID = 285590709870311132L;

    private final Pageable<T> pageable;

    public Page(Pageable<T> pageable) {
        this.pageable = pageable;
    }

    public int getPageTotal() {
        return pageable.getPageTotal();
    }

    public List<T> getContent() {
        return pageable.getContent();
    }

    public int getCurrentPage() {
        return pageable.getCurrentPage();
    }

    public int getEntityTotal() {
        return pageable.getEntityTotal();
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
