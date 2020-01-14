package com.cheney.utils.pagingTask;

import com.cheney.system.page.Limit;
import com.cheney.utils.pagingTask.exception.ConcurrentTaskException;
import com.cheney.utils.pagingTask.function.BlockTask;
import com.cheney.utils.pagingTask.function.BlockTaskWithResult;
import com.cheney.utils.pagingTask.function.CountFunction;
import com.cheney.utils.pagingTask.function.FindDataFunction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.*;

/**
 * 使用{@link ArrayBlockingQueue}提供生产消费功能，主线程负责查询数据，线程池线程负责消费数据
 *
 * @author cheney
 * @date 2020-01-14
 */
public class ArrayBlockTaskDealer {

    /**
     * 默认线程个数
     */
    private final static int DEFAULT_THREAD_NUM = 4;

    /**
     * 线程个数
     */
    private int threadNum;

    /**
     * 结束标识
     */
    private volatile boolean finish;

    public ArrayBlockTaskDealer() {
        threadNum = DEFAULT_THREAD_NUM;
        finish = true;
    }

    public ArrayBlockTaskDealer(int threadNum) {
        this.threadNum = threadNum;
        finish = true;
    }

    /**
     * 执行多线程阻塞任务，主线程生产数据，异步线程消费数据
     *
     * @param countFunction    count函数
     * @param findDataFunction 数据查询函数
     * @param blockTask        任务函数
     * @param step             步长
     * @param <T>              类型
     * @throws InterruptedException ArrayBlockingQueue导致的任务中断异常
     */
    public <T> void executeBlockTask(CountFunction countFunction, FindDataFunction<T> findDataFunction,
                                     BlockTask<T> blockTask, int step) throws InterruptedException {
        beforeTask();
        int count = countFunction.count();
        if (count < 1) {
            return;
        }

        // 初始化线程池，阻塞队列
        ExecutorService executorService = Executors.newFixedThreadPool(this.threadNum);
        ArrayBlockingQueue<List<T>> queue = new ArrayBlockingQueue<>(4);
        // 异步订阅任务
        for (int i = 0; i < this.threadNum; i++) {
            executorService.submit(() -> {
                try {
                    List<T> data;
                    while ((data = queue.poll(1, TimeUnit.SECONDS)) != null || !finish) {
                        blockTask.execute(data);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }

        // 主线程同步获取数据
        putDataToQueue(findDataFunction, step, count, queue);
        // 关闭线程池
        executorService.shutdown();
    }

    public <T, R> List<Future<List<R>>> executeBlockTask(CountFunction countFunction,
                                                         FindDataFunction<T> findDataFunction,
                                                         BlockTaskWithResult<T, R> blockTaskWithResult,
                                                         int step) throws InterruptedException {
        beforeTask();
        List<Future<List<R>>> futures = new ArrayList<>();
        int count = countFunction.count();
        if (count < 1) {
            return futures;
        }

        // 初始化线程池，阻塞队列
        ExecutorService executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_NUM);
        ArrayBlockingQueue<List<T>> queue = new ArrayBlockingQueue<>(4);
        // 异步订阅任务
        for (int i = 0; i < DEFAULT_THREAD_NUM; i++) {
            futures.add(executorService.submit(() -> {
                List<R> rs = new ArrayList<>();
                List<T> data;
                try {
                    while ((data = queue.poll(1, TimeUnit.SECONDS)) != null || !finish) {
                        rs.addAll(blockTaskWithResult.execute(data));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return rs;
            }));
        }

        // 主线程同步获取数据
        putDataToQueue(findDataFunction, step, count, queue);
        // 关闭线程池
        executorService.shutdown();

        return futures;
    }

    /**
     * 将查询结果数据入队
     *
     * @param findDataFunction 查询函数
     * @param step             步长
     * @param count            总数
     * @param queue            队列
     * @param <T>              类型
     * @throws InterruptedException
     */
    private <T> void putDataToQueue(FindDataFunction<T> findDataFunction,
                                    int step, int count,
                                    ArrayBlockingQueue<List<T>> queue) throws InterruptedException {
        Limit limit = Limit.create(0, step);
        while (count > 0) {
            if (count >= step) {
                limit.setNum(count -= step);
            } else {
                limit.setNum(0);
                limit.setSize(count);
                count -= step;
            }
            queue.put(findDataFunction.findData(limit));
        }
        finish = true;
    }

    /**
     * 任务并发安全检查
     */
    public synchronized void beforeTask() {
        if (!finish) {
            // 一个实例无法并发进行任务
            throw new ConcurrentTaskException();
        }
        finish = false;
    }

    public static void main(String[] args) throws InterruptedException {
        ArrayBlockTaskDealer taskDealer = new ArrayBlockTaskDealer();
        new Thread(() -> {
            try {
                taskDealer.executeBlockTask(() -> 5000, limit -> {
                    int num = limit.getNum();
                    ArrayList<HashMap<String, Object>> hashMaps = new ArrayList<>();
                    for (int i = 0; i < limit.getSize(); i++) {
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("num", num + i);
                        hashMaps.add(hashMap);
                    }
                    try {
                        System.out.println(hashMaps);
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return hashMaps;
                }, data -> data, 100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        taskDealer.executeBlockTask(() -> 5000, limit -> {
            int num = limit.getNum();
            ArrayList<HashMap<String, Object>> hashMaps = new ArrayList<>();
            for (int i = 0; i < limit.getSize(); i++) {
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("num", num + i);
                hashMaps.add(hashMap);
            }
            try {
                System.out.println(hashMaps);
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return hashMaps;
        }, data -> data, 100);
    }

}
