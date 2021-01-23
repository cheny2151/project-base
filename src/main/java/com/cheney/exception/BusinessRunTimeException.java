package com.cheney.exception;

import com.cheney.system.protocol.BaseResponse;
import com.cheney.system.protocol.ResponseCode;

/**
 * 业务运行时异常
 *
 * @author cheney
 * @date 2019/6/11
 */
public class BusinessRunTimeException extends RuntimeException {

    private BaseResponse<?> errorResponse;

    public BusinessRunTimeException() {
        super();
        errorResponse = BaseResponse.SERVER_ERROR;
    }

    public BusinessRunTimeException(String msg) {
        super(msg);
        this.errorResponse = BaseResponse.error(msg);
    }

    public BusinessRunTimeException(BaseResponse<?> errorResponse) {
        super(errorResponse.getMsg());
        this.errorResponse = errorResponse;
    }

    public BusinessRunTimeException(ResponseCode responseCode) {
        super(responseCode.getMsg());
        this.errorResponse = BaseResponse.error(responseCode);
    }

    public BusinessRunTimeException(int code, String msg) {
        super(msg);
        this.errorResponse = BaseResponse.error(code, msg);
    }

    public BaseResponse<?> getErrorResponse() {
        return errorResponse;
    }

}
