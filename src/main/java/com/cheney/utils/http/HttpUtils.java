package com.cheney.utils.http;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * HTTP请求工具类
 */
@Component("httpUtils")
public class HttpUtils {

    private static final HttpHeaders head;

    static {
        head = new HttpHeaders();
        head.setContentType(MediaType.APPLICATION_JSON_UTF8);
    }

    @Resource(name = "restTemplate")
    private RestTemplate restTemplate;

    /**
     * get请求，返回包括响应状态的entity
     *
     * @param url          请求地址
     * @param resultType   返回类型Class
     * @param <R>          返回类型
     * @param uriVariables url参数(替换{}占位符)
     * @return 响应体
     */
    public <R> ResponseEntity<R> getForEntity(String url, Class<R> resultType, Object... uriVariables) {
        return restTemplate.getForEntity(url, resultType, uriVariables);
    }

    /**
     * get请求，返回包括响应状态的entity
     *
     * @param url          请求地址
     * @param resultType   返回类型Class
     * @param <R>          返回类型
     * @param uriVariables url参数(替换{}占位符)
     * @return 响应体
     */
    public <R> ResponseEntity<BaseResponse<R>> getForBaseResponse(String url, ParameterizedTypeReference<BaseResponse<R>> resultType, Object... uriVariables) {
        HttpEntity<?> requestEntity = new HttpEntity<>(head);
        return restTemplate.exchange(url, HttpMethod.GET, requestEntity, resultType, uriVariables);
    }

    /**
     * get请求，直接返回body数据
     *
     * @param url          请求地址
     * @param resultType   返回类型Class
     * @param <R>          返回类型
     * @param uriVariables url参数(替换{}占位符)
     * @return 响应体
     */
    public <R> R getForObject(String url, Class<R> resultType, Object... uriVariables) {
        return restTemplate.getForObject(url, resultType, uriVariables);
    }

    /**
     * post请求,返回包括响应状态的entity
     *
     * @param url          请求地址
     * @param requestBody  请求实体
     * @param resultType   返回类型Class
     * @param uriVariables url参数(替换{}占位符)
     * @param <T>          请求类型
     * @param <R>          返回类型
     * @return 响应体
     */
    public <T, R> ResponseEntity<BaseResponse<R>> postForResponseEntity(String url, T requestBody, ParameterizedTypeReference<BaseResponse<R>> resultType, Object... uriVariables) {
        HttpEntity<T> requestEntity = wrapRequest(requestBody);
        return restTemplate.exchange(url, HttpMethod.POST, requestEntity, resultType, uriVariables);
    }

    /**
     * post请求,返回包括响应状态的entity
     *
     * @param url          请求地址
     * @param requestBody  请求实体
     * @param resultType   返回类型Class
     * @param uriVariables url参数(替换{}占位符)
     * @param <T>          请求类型
     * @param <R>          返回类型
     * @return 响应体
     */
    public <T, R> ResponseEntity<R> postForEntity(String url, T requestBody, Class<R> resultType, Object... uriVariables) {
        HttpEntity requestEntity = wrapRequest(requestBody);
        return restTemplate.postForEntity(url, requestEntity, resultType, uriVariables);
    }

    /**
     * post请求，直接返回body数据
     *
     * @param url          请求地址
     * @param requestBody  请求实体
     * @param resultType   返回类型Class
     * @param uriVariables url参数(替换{}占位符)
     * @param <T>          请求类型
     * @param <R>          返回类型
     * @return 响应体
     */
    public <T, R> R postForObject(String url, T requestBody, Class<R> resultType, Object... uriVariables) {
        HttpEntity requestEntity = wrapRequest(requestBody);
        return restTemplate.postForObject(url, requestEntity, resultType, uriVariables);
    }

    private HttpEntity wrapRequest(Object requestBody) {
        HttpEntity requestEntity;
        if (requestBody instanceof HttpEntity) {
            requestEntity = (HttpEntity) requestBody;
        } else {
            //默认为application/json;charset=utf-8请求
            requestEntity = new HttpEntity<>(requestBody, head);
        }
        return requestEntity;
    }

}
