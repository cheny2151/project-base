package com.cheney.exception;

/**
 * 请求为空时抛出异常
 */
public class RequestEmptyException extends RuntimeException {

    public RequestEmptyException() {
    }

    public RequestEmptyException(String message) {
        super(message);
    }

    public RequestEmptyException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestEmptyException(Throwable cause) {
        super(cause);
    }

    public RequestEmptyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
