package com.cheney.exception;

import com.cheney.system.protocol.BaseResponse;

/**
 * 业务异常Exception
 *
 * @author cheney
 * @date 2019-10-21
 */
public class FailRCResponseException extends RuntimeException {

    private BaseResponse<?> response;

    public FailRCResponseException(BaseResponse<?> response) {
        this.response = response;
    }

    public FailRCResponseException(String message, BaseResponse<?> response) {
        super(message);
        this.response = response;
    }

    public void setResponse(BaseResponse<?> response) {
        this.response = response;
    }

    public BaseResponse<?> getResponse() {
        return response;
    }

}
