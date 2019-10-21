package com.cheney.exception;

import com.cheney.system.response.ResponseCode;

/**
 * Http请求异常，抛出后由controller通知器统一处理
 */
public class FailHttpStatusResponseException extends RuntimeException {

    private ResponseCode code;

    public FailHttpStatusResponseException(ResponseCode code) {
        this.code = code;
    }

    public FailHttpStatusResponseException(String message, ResponseCode code) {
        super(message);
        this.code = code;
    }

    public ResponseCode getCode() {
        return code;
    }
}
