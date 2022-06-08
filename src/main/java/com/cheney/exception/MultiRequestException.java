package com.cheney.exception;

import com.cheney.utils.httpclient.RequestInfo;
import org.springframework.http.HttpEntity;

public class MultiRequestException extends RuntimeException {

    private RequestInfo requestInfo;

    private HttpEntity responseEntity;

    public MultiRequestException(RequestInfo info, HttpEntity entity, Throwable e) {
        super("聚合请求失败", e);
        this.requestInfo = info;
        this.responseEntity = entity;
    }

    public MultiRequestException(RequestInfo info, HttpEntity entity) {
        super("聚合请求失败");
        this.requestInfo = info;
        this.responseEntity = entity;
    }

    public MultiRequestException(RequestInfo info, Exception e) {
        super("聚合请求失败", e);
        this.requestInfo = info;
    }

    public MultiRequestException(Throwable cause) {
        super("聚合请求失败", cause);
    }

    public RequestInfo getRequestInfo() {
        return requestInfo;
    }

    public HttpEntity getResponseEntity() {
        return responseEntity;
    }
}
