package com.cheney.utils.pagingTask;

import com.alibaba.fastjson.JSON;
import com.cheney.system.page.Limit;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 分页任务处理器
 *
 * @author cheney
 * @date 2019-09-12
 */
@Slf4j
public class PagingTaskDealer {

    public static void startPagingTask(CountFunction countFunction, PagingTask task, int step) {
        if (task == null) {
            throw new IllegalArgumentException("task can not be null");
        }
        int count = countFunction.count();
        if (count < 1) {
            return;
        }
        Limit limit = Limit.create(0, step);
        while (count > 0) {
            if (count >= step) {
                limit.setNum(count -= step);
            } else {
                limit.setNum(0);
                limit.setSize(count);
                count -= step;
            }
            task.execute(limit);
        }
    }

    public static <T> List<TaskResult<T>> startPagingTaskWithResult(CountFunction countFunction, PagingTaskWithResult<T> task, int step) {
        if (task == null) {
            throw new IllegalArgumentException("task can not be null");
        }
        int count = countFunction.count();
        if (count < 1) {
            return Collections.emptyList();
        }
        List<TaskResult<T>> result = new ArrayList<>();

        Limit limit = Limit.create(0, step);
        while (count > 0) {
            if (count >= step) {
                limit.setNum(count -= step);
            } else {
                limit.setNum(0);
                limit.setSize(count);
                count -= step;
            }
            try {
                result.add(TaskResult.success(task.execute(limit), limit));
            } catch (Throwable e) {
                result.add(TaskResult.fail(e, limit));
            }
        }

        return result;
    }

    /**
     * 数据库count方法函数式接口
     */
    @FunctionalInterface
    public interface CountFunction {
        int count();
    }

    /**
     * 业务任务函数式接口
     */
    @FunctionalInterface
    public interface PagingTaskWithResult<T> {
        T execute(Limit limit);
    }

    @FunctionalInterface
    public interface PagingTask {
        void execute(Limit limit);
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        startPagingTask(() -> 384, (limit) -> System.out.println(JSON.toJSONString(limit)), 100);
        List<TaskResult<String>> taskResults = startPagingTaskWithResult(() -> 384, (limit) -> "success", 100);
        taskResults.forEach(e -> System.out.println(e.getResult()));
    }

}
