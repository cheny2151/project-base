package com.cheney.exception;

import com.cheney.system.protocol.BaseResponse;
import com.cheney.system.response.ResponseCode;

/**
 * 业务异常
 *
 * @author cheney
 * @date 2019/6/11
 */
public class BusinessException extends Exception {

    private BaseResponse<?> errorResponse;

    public BusinessException() {
        super();
        this.errorResponse = BaseResponse.SERVER_ERROR;
    }

    public BusinessException(String msg) {
        super(msg);
        this.errorResponse = BaseResponse.error(msg);
    }

    public BusinessException(BaseResponse errorResponse) {
        super(errorResponse.getMsg());
        this.errorResponse = errorResponse;
    }

    public BusinessException(ResponseCode responseCode) {
        super(responseCode.getMsg());
        this.errorResponse = BaseResponse.error(responseCode);
    }

    public BusinessException(int code, String msg) {
        super(msg);
        this.errorResponse = BaseResponse.error(code, msg);
    }

    public BaseResponse getErrorResponse() {
        return errorResponse;
    }

}
