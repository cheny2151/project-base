package com.cheney.utils.pagingTask;

import com.alibaba.fastjson.JSON;
import com.cheney.system.page.Limit;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * 分页任务处理器
 *
 * @author cheney
 * @date 2019-09-12
 */
@Slf4j
public class PagingTaskDealer {

    /**
     * 分页执行任务
     *
     * @param countFunction count计数函数
     * @param task          分页任务函数
     * @param step          步长
     */
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

    /**
     * 带返回值的分页任务
     *
     * @param countFunction count计数函数
     * @param task          分页任务函数
     * @param step          步长
     * @param <T>           返回数据枚举
     * @return 返回值
     */
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
     * 切割集合执行任务
     * 将一个集合切割成多个集合，然后执行指定任务
     *
     * @param originList 原集合
     * @param task       消费者
     * @param step       一次消费个数
     * @param <T>        数据泛型
     */
    public static <T> void startSlipListTask(List<T> originList, Consumer<List<T>> task, int step) {
        PagingTaskDealer.startPagingTask(originList::size, (limit) -> {
            int num = limit.getNum();
            int size = limit.getSize();
            List<T> slipList = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                slipList.add(originList.get(num + i));
            }
            task.accept(slipList);
        }, step);
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
