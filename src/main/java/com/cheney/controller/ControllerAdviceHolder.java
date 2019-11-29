package com.cheney.controller;

import com.alibaba.fastjson.JSON;
import com.cheney.exception.BusinessRunTimeException;
import com.cheney.exception.FailHttpStatusResponseException;
import com.cheney.exception.FailRCResponseException;
import com.cheney.exception.MultiRequestException;
import com.cheney.system.databind.DateEditor;
import com.cheney.system.databind.StringEditor;
import com.cheney.system.protocol.BaseResponse;
import com.cheney.system.response.JsonMessage;
import com.cheney.system.response.ResponseCode;
import com.cheney.utils.RequestParamHolder;
import com.cheney.utils.http.RequestInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * controller统一通知
 */
@RestControllerAdvice()
@Slf4j
public class ControllerAdviceHolder {

    @Resource(name = "dateEditor")
    private DateEditor dateEditor;
    @Resource(name = "stringEditor")
    private StringEditor stringEditor;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, dateEditor);
        binder.registerCustomEditor(String.class, stringEditor);
    }

    /**
     * 业务异常
     *
     * @param e BusinessRunTimeException
     * @return 响应
     */
    @ExceptionHandler(BusinessRunTimeException.class)
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<?> BusinessRunTimeException(BusinessRunTimeException e) {
        BaseResponse errorResponse = e.getErrorResponse();
        log.info("业务异常，msg->{}，code->{}", errorResponse.getMsg(), errorResponse.getCode());
        return errorResponse;
    }

    /**
     * 透传内部服务调用业务code不为成功的响应
     *
     * @param e FailRCResponseException
     * @return 响应
     */
    @ExceptionHandler(FailRCResponseException.class)
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<?> failResponseException(FailRCResponseException e) {
        BaseResponse<?> response = e.getResponse();
        if (response == null) {
            return BaseResponse.SERVER_ERROR;
        }
        response.setRequestId(RequestParamHolder.currentRequestId().orElse(null));
        log.info("内部服务调用失败，msg->{}，response->{}", e.getMessage(), response);
        return response;
    }

    /**
     * 服务调用异常
     *
     * @param e FailHttpStatusResponseException
     * @return 响应
     */
    @ExceptionHandler(FailHttpStatusResponseException.class)
    @ResponseStatus(HttpStatus.OK)
    public BaseResponse<?> failHttpStatusResponseException(FailHttpStatusResponseException e) {
        ResponseCode code = e.getCode();
        String message = e.getMessage();
        log.info("内部服务调用失败，msg->{}，code->{}", message, code);
        return BaseResponse.error(code.getStatus(), message);
    }

    /**
     * 聚合Http请求异常拦截响应
     *
     * @param e MultiRequestException
     * @return http响应
     */
    @ExceptionHandler(MultiRequestException.class)
    @ResponseStatus(HttpStatus.OK)
    public JsonMessage multiRequestException(MultiRequestException e) {
        final RequestInfo requestInfo = e.getRequestInfo();
        final HttpEntity responseEntity = e.getResponseEntity();
        if (requestInfo != null) {
            log.error("HTTP请求异常>>>请求地址->{},请求参数->{},响应数据->{},异常error->",
                    requestInfo.getUrl(),
                    JSON.toJSONString(requestInfo.getRequestBody()),
                    responseEntity != null ? JSON.toJSONString(responseEntity.getBody()) : null, e.getCause());
        } else {
            log.error("HTTP请求异常", e.getCause());
        }
        return JsonMessage.error(e.getMessage());
    }

    /**
     * 聚合Http请求异常拦截响应
     *
     * @param e MultiRequestException
     * @return http响应
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public JsonMessage HttpRequestMethodNotSupportedException(HttpServletRequest request, HttpRequestMethodNotSupportedException e) {
        log.error("url->{}:不支持的请求,{}", request.getRequestURI(), e.getMessage());
        return JsonMessage.error(e.getMessage());
    }

    /**
     * 未知异常拦截响应
     *
     * @param e unknownException
     * @return http响应
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public BaseResponse<?> exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return BaseResponse.SERVER_ERROR;
    }

}
