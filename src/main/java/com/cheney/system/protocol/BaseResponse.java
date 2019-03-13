package com.cheney.system.protocol;

import lombok.Data;

@Data
public class BaseResponse<T> {
    /** 请求标识 */
    protected String requestId;
    protected Integer code;
    protected String msg;
    protected T data;

}
