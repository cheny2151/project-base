package com.cheney.utils.http;

import com.alibaba.fastjson.JSON;
import com.cheney.exception.FailHttpStatusResponseException;
import com.cheney.exception.FailRCResponseException;
import com.cheney.system.protocol.BaseResponse;
import com.cheney.system.response.ResponseCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP请求工具类
 */
@Slf4j
@Component("httpUtils")
public class HttpUtils {

    @Value("${log.http.show:false}")
    private Boolean isDug;

    /**
     * 默认请求头
     */
    private static final HttpHeaders defaultHeader;

    /**
     * 特殊请求头设置
     */
    private static ThreadLocal<HttpHeaders> currentHeader;

    /**
     * 账户服务头
     */
    public static Map<String, String> ACCOUNT_HEADERS;

    static {
        defaultHeader = new HttpHeaders();
        defaultHeader.setContentType(MediaType.APPLICATION_JSON_UTF8);

        currentHeader = new ThreadLocal<>();

        ACCOUNT_HEADERS = new HashMap<>();
        ACCOUNT_HEADERS.put("EXCHANGE", "AUCTION");
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
        try {
            ResponseEntity<R> responseEntity = restTemplate.getForEntity(url, resultType, uriVariables);
            if (isDug)
                log.info("请求url -> {}，responseBody -> {}", url, LogUtils.cutLog(responseEntity.getBody()));
            return responseEntity;
        } catch (Exception e) {
            log.error("url->{}请求异常，msg->{}", url, e.getMessage());
            throw e;
        }
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
    public <R, B extends BaseResponse<R>> ResponseEntity<B> getForBaseResponse(String url, ParameterizedTypeReference<B> resultType, Object... uriVariables) {
        HttpEntity<?> requestEntity = new HttpEntity<>(getHeader());
        try {
            ResponseEntity<B> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, resultType, uriVariables);
            if (isDug)
                log.info("请求url -> {}，responseBody -> {}", url, LogUtils.cutLog(responseEntity.getBody()));
            return responseEntity;
        } catch (Exception e) {
            log.error("url->{}请求异常，msg->{}", url, e.getMessage());
            throw e;
        }
    }

    /**
     * get请求，返回包括响应状态的entity
     * rc不等于200时抛出业务异常
     *
     * @param url          请求地址
     * @param resultType   返回类型Class
     * @param <R>          返回类型
     * @param uriVariables url参数(替换{}占位符)
     * @return 响应体
     * @throws FailRCResponseException
     */
    public <R, B extends BaseResponse<R>> B getForBaseResponseThrowFail(String url, ParameterizedTypeReference<B> resultType, Object... uriVariables)
            throws FailRCResponseException {
        ResponseEntity<B> response = getForBaseResponse(url, resultType, uriVariables);
        B responseBody = response.getBody();
        int statusCodeValue = response.getStatusCodeValue();
        //http请求异常
        if (statusCodeValue < 200 || statusCodeValue > 299) {
            log.error("请求url->{},uriVariables->{},返回HttpStatus->{}不为成功码,response->{}"
                    , url, JSON.toJSONString(uriVariables), statusCodeValue, LogUtils.cutLog(responseBody));
            throw new FailHttpStatusResponseException(
                    "http请求失败,HttpCode为" + statusCodeValue, ResponseCode.ERROR);
        }
        if (responseBody == null) {
            throw new FailRCResponseException("url:\"" + url + "\"请求失败,响应体为null", null);
        }
        //业务异常
        if (ResponseCode.SUCCESS.getStatus() != responseBody.getCode()) {
            throw new FailRCResponseException(responseBody);
        }
        return responseBody;
    }

    /**
     * post请求，返回包括响应状态的entity
     *
     * @param url          请求地址
     * @param requestBody  请求数据
     * @param resultType   返回类型Class
     * @param <R>          返回类型
     * @param uriVariables url参数(替换{}占位符)
     * @return 响应体
     */
    public <R, B extends BaseResponse<R>> ResponseEntity<B> postForBaseResponse(String url, Object requestBody, ParameterizedTypeReference<B> resultType, Object... uriVariables) {
        HttpEntity<?> requestEntity = wrapRequest(requestBody);
        try {
            ResponseEntity<B> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, resultType, uriVariables);
            if (isDug)
                log.info("请求url -> {}，requestBody -> {}，responseBody -> {}"
                        , url, JSON.toJSONString(requestBody), LogUtils.cutLog(responseEntity.getBody()));
            return responseEntity;
        } catch (Exception e) {
            log.error("url->{}请求异常，msg->{}", url, e.getMessage());
            throw e;
        }
    }

    /**
     * get请求，返回包括响应状态的entity
     * rc不等于200时抛出业务异常
     *
     * @param url          请求地址
     * @param requestBody  请求数据
     * @param resultType   返回类型Class
     * @param <R>          返回类型
     * @param uriVariables url参数(替换{}占位符)
     * @return 响应体
     * @throws FailRCResponseException
     */
    public <R, B extends BaseResponse<R>> B postForBaseResponseThrowFail(String url, Object requestBody, ParameterizedTypeReference<B> resultType, Object... uriVariables) throws FailRCResponseException {
        ResponseEntity<B> response = postForBaseResponse(url, requestBody, resultType, uriVariables);
        BaseResponse<R> responseBody = response.getBody();
        int statusCodeValue = response.getStatusCodeValue();
        //http请求异常
        if (statusCodeValue < 200 || statusCodeValue > 299) {
            log.error("请求url->{},requestBody->{},uriVariables->{},返回HttpStatus->{}不为成功码,response->{}"
                    , url, JSON.toJSONString(requestBody), JSON.toJSONString(uriVariables), statusCodeValue, LogUtils.cutLog(responseBody));
            throw new FailHttpStatusResponseException(
                    "http请求失败,HttpCode为" + statusCodeValue, ResponseCode.ERROR);
        }
        if (responseBody == null) {
            throw new FailRCResponseException("url:\"" + url + "\"请求失败,响应体为null", null);
        }
        //业务异常，通过controller通知器直接透传给前端
        if (ResponseCode.SUCCESS.getStatus() != responseBody.getCode()) {
            throw new FailRCResponseException("url:\"" + url + "\"请求失败", responseBody);
        }
        return response.getBody();
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
        try {
            R responseBody = restTemplate.getForObject(url, resultType, uriVariables);
            if (isDug)
                log.info("请求url -> {}，responseBody -> {}", url, LogUtils.cutLog(responseBody));
            return responseBody;
        } catch (Exception e) {
            log.error("url->{}请求异常，msg->{}", url, e.getMessage());
            throw e;
        }
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
        try {
            ResponseEntity<R> responseEntity = restTemplate.postForEntity(url, requestEntity, resultType, uriVariables);
            if (isDug)
                log.info("请求url -> {}，requestBody -> {}，responseBody -> {}"
                        , url, JSON.toJSONString(requestBody), LogUtils.cutLog(responseEntity.getBody()));
            return responseEntity;
        } catch (Exception e) {
            log.error("url->{}请求异常，msg->{}", url, e.getMessage());
            throw e;
        }
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
        try {
            R responseBody = restTemplate.postForObject(url, requestEntity, resultType, uriVariables);
            if (isDug)
                log.info("请求url -> {}，requestBody -> {}，responseBody -> {}"
                        , url, JSON.toJSONString(requestBody), LogUtils.cutLog(responseBody));
            return responseBody;
        } catch (Exception e) {
            log.error("url->{}请求异常，msg->{}", url, e.getMessage());
            throw e;
        }
    }

    private HttpEntity<?> wrapRequest(Object requestBody) {
        HttpEntity<?> requestEntity;
        if (requestBody instanceof HttpEntity) {
            requestEntity = (HttpEntity<?>) requestBody;
        } else {
            //默认为application/json;charset=utf-8请求
            requestEntity = new HttpEntity<>(requestBody, getHeader());
        }
        return requestEntity;
    }

    public static void setCurrentHeader(Map<String, String> headers) {
        if (headers == null)
            throw new NullPointerException();
        HttpHeaders httpHeaders = new HttpHeaders();
        if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
        }
        headers.forEach(httpHeaders::set);
        currentHeader.set(httpHeaders);
    }

    private static HttpHeaders getHeader() {
        HttpHeaders httpHeaders = currentHeader.get();
        return httpHeaders != null ? httpHeaders : defaultHeader;
    }

}
