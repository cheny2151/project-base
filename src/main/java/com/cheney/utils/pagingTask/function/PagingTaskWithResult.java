package com.cheney.utils.pagingTask.function;

import com.cheney.system.page.Limit;

/**
 * 业务任务函数式接口
 */
@FunctionalInterface
public interface PagingTaskWithResult<T> {
    T execute(Limit limit);
}
