package com.cheney.utils.httpclient;

import lombok.Data;

/**
 * 请求信息包装类
 *
 */
@Data
public class RequestInfo {

    /**
     * 标记
     */
    private String label;

    /**
     * 请求地址
     */
    private String url;

    /**
     * 请求体数据
     */
    private Object requestBody;

    /**
     * 预计响应实体类型
     */
    private Class resultType;

    /**
     * HTTP方法
     */
    private Method method;

    public enum Method {

        GET,

        POST

    }

    public RequestInfo(String url, Method method, Object requestBody, Class resultType, String label) {
        this.url = url;
        this.method = method;
        this.requestBody = requestBody;
        this.resultType = resultType;
        this.label = label;
    }

    public static RequestInfo getRequestInfo(String url, Class resultType, String label) {
        return new RequestInfo(url, Method.GET, null, resultType, label);
    }

    public static RequestInfo postRequestInfo(String url, Object requestBody, Class resultType, String label) {
        return new RequestInfo(url, Method.POST, requestBody, resultType, label);
    }

}
