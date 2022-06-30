package com.cheney.utils.httpclient;

import com.cheney.exception.FailRCResponseException;
import com.cheney.system.protocol.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.Map;

/**
 * HTTP请求工具类
 */
@Slf4j
public class HttpUtils {

    /**
     * 静态实例http tool
     */
    private final static HttpTool HTTP_TOOL;

    static {
        HTTP_TOOL = new HttpTool();
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
    public static <R> ResponseEntity<R> getForEntity(String url, Class<R> resultType, Object... uriVariables) {
        return HTTP_TOOL.getForEntity(url, resultType, uriVariables);
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
    public static <R> R getForObject(String url, Class<R> resultType, Object... uriVariables) {
        return HTTP_TOOL.getForObject(url, resultType, uriVariables);
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
    public static <R> ResponseEntity<R> getForEntity(String url,
                                                     ParameterizedTypeReference<R> resultType,
                                                     Object... uriVariables) {
        return HTTP_TOOL.getForEntity(url, resultType, uriVariables);
    }

    /**
     * get请求，检查http响应状态
     *
     * @param url          请求地址
     * @param resultType   返回类型Class
     * @param <R>          返回类型
     * @param uriVariables url参数(替换{}占位符)
     * @return 响应体
     */
    public static <R> R getForObjectThrowFail(String url,
                                              ParameterizedTypeReference<R> resultType,
                                              Object... uriVariables) {
        return HTTP_TOOL.getForObjectThrowFail(url, resultType, uriVariables);
    }

    /**
     * get请求，返回BaseResponse
     *
     * @param url          请求地址
     * @param resultType   返回类型Class
     * @param <R>          返回类型
     * @param uriVariables url参数(替换{}占位符)
     * @return 响应体
     */
    public static <R, B extends BaseResponse<R>> B getForBaseResponse(String url,
                                                                      ParameterizedTypeReference<B> resultType,
                                                                      Object... uriVariables) {
        return HTTP_TOOL.getForBaseResponse(url, resultType, uriVariables);
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
    public static <R, B extends BaseResponse<R>> B getForBaseResponseThrowFail(String url,
                                                                               ParameterizedTypeReference<B> resultType,
                                                                               Object... uriVariables) {
        return HTTP_TOOL.getForBaseResponseThrowFail(url, resultType, uriVariables);
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
    public static <T, R> ResponseEntity<R> postForEntity(String url, T requestBody, Class<R> resultType, Object... uriVariables) {
        return HTTP_TOOL.postForEntity(url, requestBody, resultType, uriVariables);
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
    public static <T, R> R postForObject(String url, T requestBody, Class<R> resultType, Object... uriVariables) {
        return HTTP_TOOL.postForObject(url, requestBody, resultType, uriVariables);
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
    public static <R> ResponseEntity<R> postForEntity(String url, Object requestBody,
                                                      ParameterizedTypeReference<R> resultType,
                                                      Object... uriVariables) {
        return HTTP_TOOL.postForEntity(url, requestBody, resultType, uriVariables);
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
    public static <R> R postForObjectThrowFail(String url, Object requestBody,
                                               ParameterizedTypeReference<R> resultType,
                                               Object... uriVariables) {
        return HTTP_TOOL.postForObjectThrowFail(url, requestBody, resultType, uriVariables);
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
    public static <R, B extends BaseResponse<R>> B postForBaseResponse(String url, Object requestBody,
                                                                       ParameterizedTypeReference<B> resultType,
                                                                       Object... uriVariables) {
        return HTTP_TOOL.postForBaseResponse(url, requestBody, resultType, uriVariables);
    }

    /**
     * post请求，返回包括响应状态的entity
     * rc不等于200时抛出业务异常
     *
     * @param url          请求地址
     * @param requestBody  请求数据
     * @param resultType   返回类型Class
     * @param <R>          返回类型
     * @param uriVariables url参数(替换{}占位符)
     * @return 响应体
     */
    public static <R, B extends BaseResponse<R>> B postForBaseResponseThrowFail(String url, Object requestBody,
                                                                                ParameterizedTypeReference<B> resultType,
                                                                                Object... uriVariables) {
        return HTTP_TOOL.postForBaseResponseThrowFail(url, requestBody, resultType, uriVariables);
    }

    /**
     * http请求，返回包括响应状态的entity
     *
     * @param url          请求地址
     * @param requestBody  请求数据
     * @param resultType   返回类型Class
     * @param <R>          返回类型
     * @param uriVariables url参数(替换{}占位符)
     * @return 响应体
     */
    public static <R> ResponseEntity<R> forEntity(HttpMethod method, String url, Object requestBody,
                                                  ParameterizedTypeReference<R> resultType,
                                                  Object... uriVariables) {
        return HTTP_TOOL.forEntity(method, url, requestBody, resultType, uriVariables);
    }

    /**
     * http请求，返回包括响应状态的entity
     *
     * @param url          请求地址
     * @param requestBody  请求数据
     * @param resultType   返回类型Class
     * @param <R>          返回类型
     * @param uriVariables url参数(替换{}占位符)
     * @return 响应体
     */
    public <R> ResponseEntity<R> forEntity(HttpMethod method, String url, Object requestBody,
                                           Class<R> resultType, Object... uriVariables) {
        return HTTP_TOOL.forEntity(method, url, requestBody, resultType, uriVariables);
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
    public static <R> R forObjectThrowFail(HttpMethod method, String url, Object requestBody,
                                           ParameterizedTypeReference<R> resultType,
                                           Object... uriVariables) {
        return HTTP_TOOL.forObjectThrowFail(method, url, requestBody, resultType, uriVariables);
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
    public <R> R forObjectThrowFail(HttpMethod method, String url, Object requestBody,
                                    Class<R> resultType,
                                    Object... uriVariables) {
        return HTTP_TOOL.forObjectThrowFail(method, url, requestBody, resultType, uriVariables);
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
    public static <R, B extends BaseResponse<R>> B forBaseResponse(HttpMethod method, String url, Object requestBody,
                                                                   ParameterizedTypeReference<B> resultType,
                                                                   Object... uriVariables) {
        return HTTP_TOOL.forBaseResponse(method, url, requestBody, resultType, uriVariables);
    }

    /**
     * post请求，返回包括响应状态的entity
     * rc不等于200时抛出业务异常
     *
     * @param url          请求地址
     * @param requestBody  请求数据
     * @param resultType   返回类型Class
     * @param <R>          返回类型
     * @param uriVariables url参数(替换{}占位符)
     * @return 响应体
     */
    public static <R, B extends BaseResponse<R>> B forBaseResponseThrowFail(HttpMethod method, String url, Object requestBody,
                                                                            ParameterizedTypeReference<B> resultType,
                                                                            Object... uriVariables) {
        return HTTP_TOOL.forBaseResponseThrowFail(method, url, requestBody, resultType, uriVariables);
    }

    /**
     * 下载文件
     *
     * @param fileUrl 目标文件地址
     * @return 文件
     */
    public static File downloadFile(String fileUrl) {
        return HTTP_TOOL.downloadFile(fileUrl);
    }

    /**
     * 下载文件
     *
     * @param fileUrl 目标文件地址
     * @param path    下载目录
     * @return 文件
     */
    public static File downloadFile(String fileUrl, String path) {
        return HTTP_TOOL.downloadFile(fileUrl, path);
    }


    /**
     * 设置自定义请求头
     * 注意:设置的请求头只能使用一次，被使用后（发送一次http请求）移除自定义请求头
     *
     * @param headers 请求头
     */
    public static void setCurrentHeader(Map<String, String> headers) {
        HTTP_TOOL.currentHeader(headers);
    }

    /**
     * 添加自定义请求头
     * 注意:设置的请求头只能使用一次，被使用后（发送一次http请求）移除自定义请求头
     *
     * @param key   请求头key
     * @param value 请求头value
     */
    public static void addHeader(String key, String value) {
        HTTP_TOOL.addHeader(key, value);
    }

    /**
     * 设置请求头content type
     * 注意:设置的请求头只能使用一次，被使用后（发送一次http请求）移除自定义请求头
     *
     * @param contentType 请求头content type
     */
    public static void setContentType(String contentType) {
        HTTP_TOOL.contentType(contentType);
    }

    /**
     * 设置是否打印请求日志
     * 默认为false
     *
     * @param showLog 是否打印请求日志
     */
    public static void showLog(boolean showLog) {
        HTTP_TOOL.showLog(showLog);
    }

    /**
     * 设置是否抛出业务异常
     *
     * @param throwRcFail 是否抛出response code业务异常
     */
    public static void throwRcFail(boolean throwRcFail) {
        HTTP_TOOL.throwRcFail(throwRcFail);
    }

}
