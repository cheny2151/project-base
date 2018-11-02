package com.cheney.utils.http;

import lombok.Data;
import org.springframework.http.ResponseEntity;

/**
 * 聚合请求响应包装类
 */
@Data
public class ResponseInfo {

    private Class resultType;

    private ResponseEntity responseEntity;

    public ResponseInfo(Class resultType, ResponseEntity responseEntity) {
        this.resultType = resultType;
        this.responseEntity = responseEntity;
    }

}
