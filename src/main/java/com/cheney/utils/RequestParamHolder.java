package com.cheney.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cheney.exception.RequestEmptyException;
import com.cheney.system.page.Pageable;
import com.cheney.system.protocol.BaseRequest;

import java.util.Optional;

/**
 * 可获取当前请求的参数
 */
public class RequestParamHolder {

    private static InheritableThreadLocal<BaseRequest<JSONObject>> requestParam = new InheritableThreadLocal<>();

    public static void setRequestParam(BaseRequest<JSONObject> requestParam) {
        RequestParamHolder.requestParam.set(requestParam);
    }

    public static BaseRequest<JSONObject> currentRequestParam() {
        return requestParam.get();
    }

    public static Optional<String> currentRequestId() {
        BaseRequest<JSONObject> request = currentRequestParam();
        return Optional.ofNullable(request == null ? null : request.getRequestId());
    }

    public static Optional<Long> currentTimestamp() {
        BaseRequest<JSONObject> request = currentRequestParam();
        if (request == null) {
            return Optional.empty();
        }
        final String timestamp;
        return (timestamp = request.getTimestamp()) == null ?
                Optional.empty() : Optional.of(Long.valueOf(timestamp));
    }

    public static <T> T currentParam(Class<T> clazz) {
        BaseRequest<JSONObject> request = currentRequestParam();
        if (request == null) {
            throw new RequestEmptyException();
        }
        return JSON.parseObject(
                JSON.toJSONString(request.getData()), clazz
        );
    }

    public static JSONObject currentParam() {
        BaseRequest<JSONObject> request = currentRequestParam();
        if (request == null) {
            throw new RequestEmptyException();
        }
        return request.getData();
    }

    public static Pageable currentPage() {
        BaseRequest<JSONObject> request = currentRequestParam();
        return request == null ? new Pageable() : currentRequestParam().getRequiredPageable();
    }

    public static void remove() {
        requestParam.remove();
    }
}
