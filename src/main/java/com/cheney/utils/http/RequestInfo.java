package com.cheney.utils.http;

import lombok.Data;

/**
 * 请求信息包装类
 *
 * @param <R> 响应实体类型
 */
@Data
public class RequestInfo<R> {

    private String url;

    private Object requestBody;

    private Class<R> resultType;

    private Method method;

    public enum Method {

        GET,

        POST

    }

    public RequestInfo(String url, Method method, Object requestBody, Class<R> resultType) {
        this.url = url;
        this.method = method;
        this.requestBody = requestBody;
        this.resultType = resultType;
    }

    public static <R> RequestInfo<R> getRequestInfo(String url, Class<R> resultType) {
        return new RequestInfo<>(url, Method.GET, null, resultType);
    }

    public static <R> RequestInfo<R> postRequestInfo(String url, Object requestBody, Class<R> resultType) {
        return new RequestInfo<>(url, Method.POST, requestBody, resultType);
    }

}
