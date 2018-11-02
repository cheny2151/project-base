package com.cheney.controller;

import com.alibaba.fastjson.JSON;
import com.cheney.exception.MultiRequestException;
import com.cheney.system.databind.DateEditor;
import com.cheney.system.databind.StringEditor;
import com.cheney.system.message.JsonMessage;
import com.cheney.utils.http.RequestInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.annotation.Resource;
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

    @ExceptionHandler({UsernameNotFoundException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public JsonMessage UsernameNotFoundException(UsernameNotFoundException e) {
        log.info(e.getMessage(), e);
        return JsonMessage.error("username not found");
    }

    /**
     * 聚合Http请求异常拦截响应
     *
     * @param e MultiRequestException
     * @return http响应
     */
    @ExceptionHandler(value = MultiRequestException.class)
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
     * 未知异常拦截响应
     *
     * @param e unknownException
     * @return http响应
     */
    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public JsonMessage exceptionHandler(Exception e) {
        log.error(e.getMessage(), e);
        return JsonMessage.error(e.getMessage());
    }

}
