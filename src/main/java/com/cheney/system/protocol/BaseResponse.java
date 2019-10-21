package com.cheney.system.protocol;

import com.cheney.system.response.ResponseCode;
import com.cheney.utils.RequestParamHolder;
import lombok.Data;

import java.util.HashMap;

@Data
public class BaseResponse<T> {
    /** 请求标识 */
    protected String requestId;
    protected Integer code;
    protected String msg;
    protected T data;

    public final static BaseResponse SERVER_ERROR = error(ResponseCode.ERROR);

    /**
     * 成功请求，包装数据
     *
     * @param data 单个数据或者K:V对
     * @return BaseResponse
     */
    public static BaseResponse<?> success(Object... data) {
        BaseResponse baseResponse = new BaseResponse<>();
        int len = data.length;
        if (len == 1) {
            baseResponse.setData(data[0]);
        } else if (len > 1) {
            if ((len & 1) == 1) {
                throw new IllegalArgumentException("args must be even");
            }
            HashMap<Object, Object> dataMap = new HashMap<>();
            for (int i = 0; i < len; i++) {
                dataMap.put(data[i++], data[i]);
            }
            baseResponse.setData(dataMap);
        }
        baseResponse.setResponseCode(ResponseCode.SUCCESS);
        return baseResponse;
    }

    public static BaseResponse error(ResponseCode code) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setResponseCode(code);
        return baseResponse;
    }

    public static BaseResponse error(ResponseCode code, String msg) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setResponseCode(code);
        baseResponse.setMsg(msg);
        return baseResponse;
    }

    public static BaseResponse error(int code, String msg) {
        BaseResponse baseResponse = new BaseResponse();
        baseResponse.setCode(code);
        baseResponse.setMsg(msg);
        return baseResponse;
    }

    public void setResponseCode(ResponseCode code) {
        this.msg = code.getMsg();
        this.code = code.getStatus();
    }

    /**
     * 响应requestId统一处理
     *
     * @return requestId
     */
    public String getRequestId() {
        return RequestParamHolder.currentRequestId().orElse(null);
    }

}
