package com.cheney.utils.pagingTask.function;

import com.cheney.system.page.Limit;

/**
 * @author cheney
 * @date 2020-01-14
 */
@FunctionalInterface
public interface PagingTask {
    void execute(Limit limit);
}
