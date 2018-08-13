package com.cheney.system.page;

import com.cheney.entity.dto.BaseEntity;

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

    private long total;

    private int pageNumber;

    private int pageSize;

    public Page() {
    }

    public Page(List<T> content, long total, PageInfo pageInfo) {
        this.content = content;
        this.total = total;
        this.pageNumber = pageInfo.getPageNumber();
        this.pageSize = pageInfo.getPageSize();
    }


    public List<T> getContent() {
        return content;
    }

    public long getTotal() {
        return total;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    /**
     * 生成一个空列表的分页
     */
    public static <T extends BaseEntity> Page<T> emptyPage(Pageable pageable) {
        return new Page<>(new ArrayList<>(), 0L, pageable);
    }
}
