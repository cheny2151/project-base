package com.cheney.utils.pagingTask;

import com.alibaba.fastjson.JSON;
import com.cheney.system.page.Limit;

/**
 * 分页任务处理器
 *
 * @author cheney
 * @date 2019-09-12
 */
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
    public interface PagingTask {
        void execute(Limit limit);
    }

    /**
     * 测试方法
     */
    public static void main(String[] args) {
        startPagingTask(() -> 384, (limit) -> System.out.println(JSON.toJSONString(limit)), 100);
    }

}
