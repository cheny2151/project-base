package com.cheney.utils.pagingTask;

import com.cheney.system.page.Limit;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 分页任务处理器
 *
 * @author cheney
 * @date 2019-09-12
 */
@Slf4j
public class PagingTaskDealer {

    // 默认线程个数
    private final static int DEFAULT_THREAD_NUM = 4;

    /**
     * 分页执行任务
     *
     * @param countFunction 计数函数
     * @param task          任务
     * @param step          一次执行个数
     */
    public static void startPagingTask(CountFunction countFunction, PagingTask task, int step, boolean async) {
        if (task == null) {
            throw new IllegalArgumentException("task can not be null");
        }
        int count = countFunction.count();
        if (count < 1) {
            return;
        }

        Limit limit = Limit.create(0, step);
        ExecutorService executorService = null;
        while (count > 0) {
            if (async) {
                // 多线程并发，limit不可作为公共资源复用
                limit = Limit.create(0, step);
            }
            if (count >= step) {
                limit.setNum(count -= step);
            } else {
                limit.setNum(0);
                limit.setSize(count);
                count -= step;
            }
            if (async) {
                // 异步
                executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);
                final Limit finalLimit = limit;
                executorService.execute(() -> task.execute(finalLimit));
            } else {
                task.execute(limit);
            }
        }
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    /**
     * 分割集合执行任务
     *
     * @param originList 原集合
     * @param task   消费者
     * @param step       一次消费个数
     * @param <T>        数据泛型
     */
    public static <T> void startSlipListTask(List<T> originList, Consumer<List<T>> task, int step, boolean async) {
        startPagingTask(originList::size, (limit) -> {
            int num = limit.getNum();
            int size = limit.getSize();
            List<T> slipList = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                slipList.add(originList.get(num + i));
            }
            task.accept(slipList);
        }, step, async);
    }

    /**
     * 分页执行任务并返回结果
     *
     * @param countFunction 计数函数
     * @param task          任务
     * @param step          一次执行个数
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
     * 分页异步执行任务并返回结果
     *
     * @param countFunction 计数函数
     * @param task          任务
     * @param step          一次执行个数
     */
    public static <T> List<TaskResult<Future<T>>> startAsyncPagingTaskWithResult(CountFunction countFunction, PagingTaskWithResult<T> task, int step) {
        if (task == null) {
            throw new IllegalArgumentException("task can not be null");
        }
        int count = countFunction.count();
        if (count < 1) {
            return Collections.emptyList();
        }
        List<TaskResult<Future<T>>> result = new ArrayList<>();

        ExecutorService executorService = null;
        while (count > 0) {
            // 多线程并发，limit不可作为公共资源复用
            Limit limit = Limit.create(0, step);
            if (count >= step) {
                limit.setNum(count -= step);
            } else {
                limit.setSize(count);
                count -= step;
            }
            // 异步
            executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);
            try {
                result.add(TaskResult.success(executorService.submit(() -> task.execute(limit)), limit));
            } catch (Throwable e) {
                result.add(TaskResult.fail(e, limit));
            }
        }

        executorService.shutdown();

        return result;
    }

    /**
     * 分割集合执行任务
     *
     * @param originList 原集合
     * @param task       消费者
     * @param step       一次消费个数
     */
    public static <T, R> List<TaskResult<R>> startSlipListTaskWithResult(List<T> originList, Function<List<T>, R> task, int step) {
        return PagingTaskDealer.startPagingTaskWithResult(originList::size, getSlipListTaskWithResult(originList, task), step);
    }

    /**
     * 分割集合执行任务
     *
     * @param originList 原集合
     * @param task       消费者
     * @param step       一次消费个数
     */
    public static <T, R> List<TaskResult<Future<R>>> startSlipListAsyncTaskWithResult(List<T> originList, Function<List<T>, R> task, int step) {
        return PagingTaskDealer.startAsyncPagingTaskWithResult(originList::size, getSlipListTaskWithResult(originList, task), step);
    }

    private static <T, R> PagingTaskWithResult<R> getSlipListTaskWithResult(List<T> originList, Function<List<T>, R> task) {
        return (limit) -> {
            int num = limit.getNum();
            int size = limit.getSize();
            List<T> slipList = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                slipList.add(originList.get(num + i));
            }
            return task.apply(slipList);
        };
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

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ArrayList<String> strings = new ArrayList<>();
        strings.add("1");
        strings.add("5");
        strings.add("4");
        strings.add("3");
        strings.add("2");
//        List<TaskResult<Integer>> taskResults = startSlipListTaskWithResult(strings, list -> list.size(), 2);
//        for (TaskResult<Integer> taskResult : taskResults) {
//            System.out.println(taskResult.getResult());
//        }
        List<TaskResult<Future<Integer>>> taskResults = startSlipListAsyncTaskWithResult(strings, (limit) -> {
            System.out.println("start");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("end");
            return limit.size();
        }, 2);
        for (TaskResult<Future<Integer>> taskResult : taskResults) {
            Integer integer = taskResult.getResult().get();
            System.out.println(integer);
        }
        System.out.println("out");
    }

}
