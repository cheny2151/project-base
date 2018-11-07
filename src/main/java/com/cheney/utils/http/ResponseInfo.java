package com.cheney.utils.http;

import lombok.Data;
import org.springframework.http.ResponseEntity;

/**
 * 聚合请求响应包装类
 */
@Data
public class ResponseInfo<R> {

    /**
     * 标记
     */
    private String label;

    /**
     * 返回数据类型
     */
    private Class<R> resultType;

    /**
     * 返回实体
     */
    private ResponseEntity responseEntity;

    public ResponseInfo(Class<R> resultType, ResponseEntity<R> responseEntity, String label) {
        this.resultType = resultType;
        this.responseEntity = responseEntity;
        this.label = label;
    }

}
