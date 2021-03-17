package com.cheney.exception;

import com.cheney.system.protocol.BaseResponse;
import com.cheney.system.protocol.ResponseCode;

/**
 * 业务运行时异常
 *
 * @author cheney
 * @date 2019/6/11
 */
public class BusinessRuntimeException extends RuntimeException {

    private BaseResponse<?> errorResponse;

    public BusinessRuntimeException() {
        super();
        errorResponse = BaseResponse.SERVER_ERROR;
    }

    public BusinessRuntimeException(String msg) {
        super(msg);
        this.errorResponse = BaseResponse.error(msg);
    }

    public BusinessRuntimeException(BaseResponse<?> errorResponse) {
        super(errorResponse.getMsg());
        this.errorResponse = errorResponse;
    }

    public BusinessRuntimeException(ResponseCode responseCode) {
        super(responseCode.getMsg());
        this.errorResponse = BaseResponse.error(responseCode);
    }

    public BusinessRuntimeException(int code, String msg) {
        super(msg);
        this.errorResponse = BaseResponse.error(code, msg);
    }

    public BaseResponse<?> getErrorResponse() {
        return errorResponse;
    }

}
