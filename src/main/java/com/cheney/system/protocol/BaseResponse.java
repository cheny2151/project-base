package com.cheney.system.protocol;

import com.alibaba.fastjson.JSON;
import com.cheney.utils.RequestParamHolder;
import lombok.Data;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

@Data
public class BaseResponse<T> {
    /**
     * 请求标识
     */
    protected String requestId;
    protected Integer code;
    protected String msg;
    protected T data;

    public final static BaseResponse<?> SERVER_ERROR = error(ResponseCode.ERROR);

    public static <T> BaseResponse<T> success(T data) {
        BaseResponse<T> baseResponse = new BaseResponse<>();
        baseResponse.setResponseCode(ResponseCode.SUCCESS);
        baseResponse.setData(data);
        return baseResponse;
    }

    /**
     * 成功请求，包装数据
     *
     * @param data 单个数据或者K:V对
     * @return BaseResponse
     */
    public static BaseResponse<?> success(Object... data) {
        Object data0 = null;
        int len = data.length;
        if (len == 1) {
            data0 = data[0];
        } else if (len > 1) {
            if ((len & 1) == 1) {
                throw new IllegalArgumentException("args must be even");
            }
            HashMap<Object, Object> dataMap = new HashMap<>();
            for (int i = 0; i < len; i++) {
                dataMap.put(data[i++], data[i]);
            }
            data0 = dataMap;
        }
        return success(data0);
    }

    public static <T> BaseResponse<T> error(ResponseCode code) {
        return error(code.getStatus(), code.getMsg());
    }

    public static <T> BaseResponse<T> error(ResponseCode code, String msg) {
        return error(code.getStatus(), msg);
    }

    public static <T> BaseResponse<T> error(String msg) {
        return error(ResponseCode.ERROR.getStatus(), msg);
    }

    public static <T> BaseResponse<T> error(int code, String msg) {
        BaseResponse<T> baseResponse = new BaseResponse<>();
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
        return RequestParamHolder.requestId().orElse(null);
    }

    public void writeToResponse(HttpServletResponse response) throws IOException {
        PrintWriter writer = response.getWriter();
        String respBody = JSON.toJSONString(this);
        response.setContentType("application/json");
        writer.write(respBody);
    }


}
