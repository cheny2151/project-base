package com.cheney.utils.pagingTask.exception;

/**
 * 并发执行任务异常
 *
 * @author cheney
 * @date 2020-01-14
 */
public class ConcurrentTaskException extends RuntimeException {

    public ConcurrentTaskException() {
    }

    public ConcurrentTaskException(String message) {
        super(message);
    }
}
