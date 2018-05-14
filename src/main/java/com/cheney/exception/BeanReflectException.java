package com.cheney.exception;

/**
 * 将反射的检查类型错误转换为非检查类型错误
 */
public class BeanReflectException extends RuntimeException {

    public BeanReflectException(String message) {
        super(message);
    }

    public BeanReflectException(String message, Throwable cause) {
        super(message, cause);
    }
}
