package com.cheney.exception;

/**
 * 系统运行时异常
 *
 * @author cheney
 * @date 2019/6/11
 */
public class SystemRunTimeException extends RuntimeException {

    public SystemRunTimeException() {
    }

    public SystemRunTimeException(String message) {
        super(message);
    }

    public SystemRunTimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SystemRunTimeException(Throwable cause) {
        super(cause);
    }

    public SystemRunTimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
