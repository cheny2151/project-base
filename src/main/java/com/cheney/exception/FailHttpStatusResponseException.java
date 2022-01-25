package com.cheney.exception;

/**
 * Http请求异常，抛出后由controller通知器统一处理
 */
public class FailHttpStatusResponseException extends RuntimeException {

    private final int httpStatusCode;

    public FailHttpStatusResponseException(String message, int httpState) {
        super(message);
        this.httpStatusCode = httpState;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }
}
