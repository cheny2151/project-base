package com.cheney.utils.pagingTask;

import com.cheney.system.page.Limit;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 分页任务处理器
 * 1.1 支持回调,可选主线程或者异步执行回调任务
 *
 * @author cheney
 * @version 1.1
 * @date 2019-09-12
 */
@Slf4j
public class PagingTaskDealer {

    // 默认线程个数
    private final static int DEFAULT_THREAD_NUM = 4;

    /**
     * 分页执行任务
     *
     * @param countFunction       计数函数
     * @param task                任务
     * @param step                一次执行个数
     * @param async               是否异步
     * @param callable            异步执行任务时的回调任务
     * @param callableTaskUseMain 回调任务是否使用主线程
     */
    @SneakyThrows
    public static void startPagingTask(CountFunction countFunction, PagingTask task,
                                       int step, boolean async, Runnable callable, boolean callableTaskUseMain) {
        if (task == null) {
            throw new IllegalArgumentException("task can not be null");
        }
        int count = countFunction.count();
        if (count < 1) {
            return;
        }

        Limit limit = Limit.create(0, step);
        ExecutorService executorService = null;
        CountDownLatch countDownLatch = null;
        if (async) {
            executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);
            if (callable != null) {
                // 计算分页数，使用countDownLatch等待线程任务
                int count1 = (count + step - 1) / step;
                countDownLatch = new CountDownLatch(count1);
                final CountDownLatch finalCountDownLatch = countDownLatch;
                final PagingTask finalTask = task;
                task = (limit0) -> {
                    try {
                        finalTask.execute(limit0);
                    } finally {
                        finalCountDownLatch.countDown();
                    }
                };
            }
        }
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
                final Limit finalLimit = limit;
                final PagingTask _finalTask = task;
                executorService.execute(() -> _finalTask.execute(finalLimit));
            } else {
                task.execute(limit);
            }
        }
        if (async && callable != null) {
            // 存在回调任务
            countDownLatch.await();
            if (callableTaskUseMain) {
                // 使用主线程执行回调任务
                callable.run();
            } else {
                // 使用线程池执行回调任务
                executorService.submit(callable);
            }
        }
        if (executorService != null) {
            // 关闭线程池
            executorService.shutdown();
        }
    }


    /**
     * 异步分页执行任务
     *
     * @param countFunction 计数函数
     * @param task          任务
     * @param step          一次执行个数
     */
    public static void startAsyncPagingTask(CountFunction countFunction, PagingTask task, int step,
                                            Runnable callable, boolean callableTaskUseMain) {
        startPagingTask(countFunction, task, step, true, callable, callableTaskUseMain);
    }

    /**
     * 分页执行任务
     *
     * @param countFunction 计数函数
     * @param task          任务
     * @param step          一次执行个数
     */
    public static void startPagingTask(CountFunction countFunction, PagingTask task, int step) {
        startPagingTask(countFunction, task, step, false, null, false);
    }

    /**
     * 分割集合执行任务
     *
     * @param originList 原集合
     * @param task       消费者
     * @param step       一次消费个数
     * @param <T>        数据泛型
     */
    public static <T> void startAsyncSlipListTask(List<T> originList, Consumer<List<T>> task, int step,
                                                  Runnable callable, boolean callableTaskUseMain) {
        startPagingTask(originList::size, (limit) -> {
            int num = limit.getNum();
            int size = limit.getSize();
            List<T> slipList = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                slipList.add(originList.get(num + i));
            }
            task.accept(slipList);
        }, step, true, callable, callableTaskUseMain);
    }

    /**
     * 分割集合执行任务
     *
     * @param originList 原集合
     * @param task       消费者
     * @param step       一次消费个数
     * @param <T>        数据泛型
     */
    public static <T> void startSlipListTask(List<T> originList, Consumer<List<T>> task, int step) {
        startPagingTask(originList::size, (limit) -> {
            int num = limit.getNum();
            int size = limit.getSize();
            List<T> slipList = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                slipList.add(originList.get(num + i));
            }
            task.accept(slipList);
        }, step, false, null, false);
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

        ExecutorService executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);
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
