package com.cheney.utils;

import cn.cheny.toolbox.other.map.EasyMap;
import cn.cheny.toolbox.other.page.Pageable;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cheney.exception.RequestEmptyException;
import com.cheney.system.protocol.BaseRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

/**
 * 可获取当前请求的参数
 */
public class RequestParamHolder {

    private static final InheritableThreadLocal<BaseRequest<?>> requestParam = new InheritableThreadLocal<>();
    private static final InheritableThreadLocal<HttpServletRequest> currentRequest = new InheritableThreadLocal<>();
    private static final InheritableThreadLocal<HttpServletResponse> currentResponse = new InheritableThreadLocal<>();

    public static BaseRequest<?> request() {
        return requestParam.get();
    }

    public static Optional<String> requestId() {
        BaseRequest<?> request = request();
        return Optional.ofNullable(request == null ? null : request.getRequestId());
    }

    public static Optional<Long> timestamp() {
        BaseRequest<?> request = request();
        if (request == null) {
            return Optional.empty();
        }
        final String timestamp;
        return (timestamp = request.getTimestamp()) == null ?
                Optional.empty() : Optional.of(Long.valueOf(timestamp));
    }

    public static Object data() {
        BaseRequest<?> request = request();
        if (request == null) {
            throw new RequestEmptyException();
        }
        return request.getData();
    }

    public static <T> T data(Class<T> clazz) {
        BaseRequest<?> request = request();
        if (request == null) {
            throw new RequestEmptyException();
        }
        return JSON.parseObject(
                JSON.toJSONString(request.getData()), clazz
        );
    }

    public static EasyMap dataAsEasyMap() {
        Object data = data();
        if (data instanceof EasyMap) {
            return (EasyMap) data;
        }
        return new EasyMap(dataAsJSONObject());
    }

    public static JSONObject dataAsJSONObject() {
        Object data = data();
        if (data instanceof JSONObject) {
            return (JSONObject) data;
        }
        return data(JSONObject.class);
    }

    public static JSONArray dataAsJSONArray() {
        Object data = data();
        if (data instanceof JSONArray) {
            return (JSONArray) data;
        }
        return data(JSONArray.class);
    }

    public static Pageable page() {
        BaseRequest<?> request = request();
        return request == null ? new Pageable() : request().requiredPageable();
    }

    /**
     * 将当前请求参数data放入Pageable中Filter的otherFilter字段
     * 作为附加过滤条件
     */
    public static Pageable pageWithDataParams() {
        Pageable pageable = page();
        pageable.addOtherParams(dataAsJSONObject());
        return pageable;
    }

    public static void setRequestParam(BaseRequest<?> requestParam) {
        RequestParamHolder.requestParam.set(requestParam);
    }

    public static void setCurrentRequest(HttpServletRequest httpServletRequest) {
        currentRequest.set(httpServletRequest);
    }

    public static void setCurrentResponse(HttpServletResponse httpServletResponse) {
        currentResponse.set(httpServletResponse);
    }

    public static HttpServletRequest currentRequest() {
        return currentRequest.get();
    }

    public static HttpServletResponse currentResponse() {
        return currentResponse.get();
    }

    public static void remove() {
        requestParam.remove();
        currentRequest.remove();
        currentResponse.remove();
    }
}
