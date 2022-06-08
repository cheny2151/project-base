package com.cheney.utils.http;

import cn.cheny.toolbox.other.map.EasyMap;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cheney.exception.RequestEmptyException;
import com.cheney.utils.RandomIdGenerator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.format.DateTimeFormatter;

/**
 * 可获取当前请求的参数
 */
public class RequestParamHolderWithoutBaseRequest {

    private static final int ID_NUM_SIZE = 5;
    private static final DateTimeFormatter ID_TIME_FORMAT = DateTimeFormatter.ofPattern("yyMMddHHmm");

    private static final InheritableThreadLocal<Object> requestParam = new InheritableThreadLocal<>();
    private static final InheritableThreadLocal<HttpServletRequest> currentRequest = new InheritableThreadLocal<>();
    private static final InheritableThreadLocal<HttpServletResponse> currentResponse = new InheritableThreadLocal<>();
    private static final InheritableThreadLocal<String> innerReqId = new InheritableThreadLocal<>();

    public static Object request() {
        return requestParam.get();
    }

    public static <T> T request(Class<T> clazz) {
        Object request = request();
        if (request == null) {
            throw new RequestEmptyException();
        }
        return JSON.parseObject(
                JSON.toJSONString(request), clazz
        );
    }

    public static EasyMap dataAsEasyMap() {
        Object data = request();
        if (data instanceof EasyMap) {
            return (EasyMap) data;
        }
        return new EasyMap(dataAsJSONObject());
    }

    public static JSONObject dataAsJSONObject() {
        Object data = request();
        if (data instanceof JSONObject) {
            return (JSONObject) data;
        }
        return request(JSONObject.class);
    }

    public static JSONArray dataAsJSONArray() {
        Object data = request();
        if (data instanceof JSONArray) {
            return (JSONArray) data;
        }
        return request(JSONArray.class);
    }

    public static void setRequestParam(Object requestParam) {
        RequestParamHolderWithoutBaseRequest.requestParam.set(requestParam);
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

    public static String getInnerId() {
        return innerReqId.get();
    }

    public static void remove() {
        requestParam.remove();
        currentRequest.remove();
        currentResponse.remove();
        innerReqId.remove();
    }

    public static void generateInnerId() {
        try {
            String id = RandomIdGenerator.generateWithTime(ID_TIME_FORMAT, ID_NUM_SIZE);
            innerReqId.set(id);
        } catch (Exception e) {
            // do nothing
        }
    }
}
