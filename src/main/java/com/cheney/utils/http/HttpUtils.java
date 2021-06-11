package com.cheney.utils.http;

import com.cheney.exception.FailHttpStatusResponseException;
import com.cheney.exception.FailRCResponseException;
import com.cheney.system.protocol.BaseResponse;
import com.cheney.system.protocol.ResponseCode;
import com.cheney.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * HTTP请求工具类
 */
@Slf4j
public class HttpUtils {

    /**
     * 系统临时文件目录
     */
    private final static String TMPDIR = System.getProperty("java.io.tmpdir");

    /**
     * 是否开启dug
     */
    private static boolean DUG = false;

    /**
     * 是否抛出response code异常
     */
    private static boolean THROW_RC_FAIL = false;

    /**
     * 默认请求头
     */
    private static final HttpHeaders defaultHeader;

    /**
     * 特殊请求头设置
     */
    private static final ThreadLocal<Map<String, String>> currentHeader;

    /**
     * http请求template
     */
    private final static RestTemplate REST_TEMPLATE;

    static {
        defaultHeader = new HttpHeaders();
        defaultHeader.setContentType(MediaType.APPLICATION_JSON);

        currentHeader = new ThreadLocal<>();

        // 初始化template
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5 * 1000);
        requestFactory.setReadTimeout(10 * 1000);
        requestFactory.setHttpClient(HttpClientBuilderSupport.httpClientBuilder().build());
        REST_TEMPLATE = new RestTemplate(requestFactory);
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
        try {
            ResponseEntity<R> responseEntity = getTemplate(url).getForEntity(url, resultType, uriVariables);
            if (DUG)
                log.info("请求url -> {}，responseBody -> {}", url, LogUtils.cutLog(responseEntity.getBody()));
            return responseEntity;
        } catch (Exception e) {
            log.error("url->{}请求异常,params->{}，msg->{}", url, uriVariables, e.getMessage());
            throw e;
        }
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
        try {
            R responseBody = getTemplate(url).getForObject(url, resultType, uriVariables);
            if (DUG)
                log.info("请求url -> {}，responseBody -> {}", url, LogUtils.cutLog(responseBody));
            return responseBody;
        } catch (Exception e) {
            log.error("url->{}请求异常,params->{}，msg->{}", url, uriVariables, e.getMessage());
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
    public static <R> ResponseEntity<R> getForEntity(String url,
                                                     ParameterizedTypeReference<R> resultType,
                                                     Object... uriVariables) {
        return forEntity(HttpMethod.GET, url, null, resultType, uriVariables);
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
        return forObjectThrowFail(HttpMethod.GET, url, null, resultType, uriVariables);
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
    public static <R, B extends BaseResponse<R>> ResponseEntity<B> getForBaseResponse(String url,
                                                                                      ParameterizedTypeReference<B> resultType,
                                                                                      Object... uriVariables) {
        return forBaseResponse(HttpMethod.GET, url, null, resultType, uriVariables);
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
        return forBaseResponseThrowFail(HttpMethod.GET, url, null, resultType, uriVariables);
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
        HttpEntity<T> requestEntity = wrapRequest(requestBody);
        try {
            ResponseEntity<R> responseEntity = getTemplate(url).postForEntity(url, requestEntity, resultType, uriVariables);
            if (DUG)
                log.info("请求url -> {}，requestBody -> {}，responseBody -> {}"
                        , url, JsonUtils.toJson(requestBody), LogUtils.cutLog(responseEntity.getBody()));
            return responseEntity;
        } catch (Exception e) {
            log.error("url->{}请求异常,request body->{},params->{}，msg->{}",
                    url, JsonUtils.toJson(requestBody), uriVariables, e.getMessage());
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
    public static <T, R> R postForObject(String url, T requestBody, Class<R> resultType, Object... uriVariables) {
        HttpEntity<T> requestEntity = wrapRequest(requestBody);
        try {
            R responseBody = getTemplate(url).postForObject(url, requestEntity, resultType, uriVariables);
            if (DUG)
                log.info("请求url -> {}，requestBody -> {}，responseBody -> {}"
                        , url, JsonUtils.toJson(requestBody), LogUtils.cutLog(responseBody));
            return responseBody;
        } catch (Exception e) {
            log.error("url->{}请求异常,request body->{}，msg->{}", url, JsonUtils.toJson(requestBody), e.getMessage());
            throw e;
        }
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
        return forEntity(HttpMethod.POST, url, requestBody, resultType, uriVariables);
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
        return forObjectThrowFail(HttpMethod.POST, url, requestBody, resultType, uriVariables);
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
    public static <R, B extends BaseResponse<R>> ResponseEntity<B> postForBaseResponse(String url, Object requestBody,
                                                                                       ParameterizedTypeReference<B> resultType,
                                                                                       Object... uriVariables) {
        return forBaseResponse(HttpMethod.POST, url, requestBody, resultType, uriVariables);
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
        return forBaseResponseThrowFail(HttpMethod.POST, url, requestBody, resultType, uriVariables);
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
        HttpEntity<?> requestEntity = wrapRequest(requestBody);
        try {
            ResponseEntity<R> responseEntity = getTemplate(url).exchange(url, method, requestEntity, resultType, uriVariables);
            if (DUG)
                log.info("请求url -> {}，requestBody -> {}，responseBody -> {}"
                        , url, JsonUtils.toJson(requestBody), LogUtils.cutLog(responseEntity.getBody()));
            return responseEntity;
        } catch (Exception e) {
            log.error("url->{}请求异常,request body->{},params->{}，msg->{}",
                    url, JsonUtils.toJson(requestBody), uriVariables, e.getMessage());
            throw e;
        }
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
        ResponseEntity<R> response = forEntity(method, url, requestBody, resultType, uriVariables);
        checkRespStatus(method, url, requestBody, uriVariables, response);
        return response.getBody();
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
    public static <R, B extends BaseResponse<R>> ResponseEntity<B> forBaseResponse(HttpMethod method, String url, Object requestBody,
                                                                                   ParameterizedTypeReference<B> resultType,
                                                                                   Object... uriVariables) {
        return forEntity(method, url, requestBody, resultType, uriVariables);
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
        ResponseEntity<B> response = forBaseResponse(method, url, requestBody, resultType, uriVariables);
        checkRespStatus(method, url, requestBody, uriVariables, response);
        if (THROW_RC_FAIL) {
            BaseResponse<R> responseBody = response.getBody();
            if (responseBody == null) {
                throw new FailRCResponseException("url:\"" + url + "\"请求失败,响应体为null", null);
            }
            //业务异常，通过controller通知器直接透传给前端
            if (ResponseCode.SUCCESS.getStatus() != responseBody.getCode()) {
                throw new FailRCResponseException("url:\"" + url + "\"请求失败:" + responseBody.getMsg(), responseBody);
            }
        }
        return response.getBody();
    }

    /**
     * 校验http响应状态码
     */
    private static <R> void checkRespStatus(HttpMethod method, String url, Object requestBody,
                                            Object[] uriVariables, ResponseEntity<R> response) {
        int statusCode = response.getStatusCodeValue();
        //http请求异常
        if (statusCode < 200 || statusCode > 299) {
            log.error("[{}]请求url->{},requestBody->{},uriVariables->{},返回HttpStatus->{}不为成功码,response->{}"
                    , method.name(), url,
                    JsonUtils.toJson(requestBody), JsonUtils.toJson(uriVariables), statusCode,
                    LogUtils.cutLog(response.getBody()));
            throw new FailHttpStatusResponseException(
                    "http请求失败,http status为" + statusCode, ResponseCode.ERROR);
        }
    }

    /**
     * 设置自定义请求头
     * 注意:设置的请求头只能使用一次，被使用后（发送一次http请求）移除自定义请求头
     *
     * @param headers 请求头
     */
    public static void setCurrentHeader(Map<String, String> headers) {
        if (headers == null)
            throw new NullPointerException();
        currentHeader.set(headers);
    }

    /**
     * 添加自定义请求头
     * 注意:设置的请求头只能使用一次，被使用后（发送一次http请求）移除自定义请求头
     *
     * @param key   请求头key
     * @param value 请求头value
     */
    public static void addHeader(String key, String value) {
        if (key != null && value != null) {
            Map<String, String> headers = currentHeader.get();
            if (headers == null) {
                headers = new HashMap<>();
                currentHeader.set(headers);
            }
            headers.put(key, value);
        }
    }

    /**
     * 设置请求头content type
     * 注意:设置的请求头只能使用一次，被使用后（发送一次http请求）移除自定义请求头
     *
     * @param contentType 请求头content type
     */
    public static void setContentType(String contentType) {
        addHeader("Content-Type", contentType);
    }

    /**
     * CloseableHttpClient发送简单的post请求
     *
     * @param url 请求url
     */
    public static String simpleGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        return simpleExecute(url, httpGet);
    }

    /**
     * CloseableHttpClient发送简单的post请求
     *
     * @param url         请求url
     * @param requestBody 请求数据
     * @param contentType content-type
     */
    public static String simplePost(String url, Object requestBody, ContentType contentType) {
        HttpPost httpPost = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(JsonUtils.toJson(requestBody), contentType);
        httpPost.setEntity(stringEntity);
        return simpleExecute(url, httpPost);
    }

    /**
     * 下载文件
     *
     * @param fileUrl 目标文件地址
     * @return 文件
     */
    public static File downloadFile(String fileUrl) {
        return downloadFile(fileUrl, TMPDIR);
    }

    /**
     * 下载文件
     *
     * @param fileUrl 目标文件地址
     * @param path    下载目录
     * @return 文件
     */
    public static File downloadFile(String fileUrl, String path) {
        RestTemplate template = getTemplate(fileUrl);
        RequestCallback requestCallback = req -> {
            HttpHeaders headers = req.getHeaders();
            Map<String, String> ch = currentHeader.get();
            if (ch != null) {
                currentHeader.remove();
                ch.forEach(headers::set);
            }
        };
        ResponseExtractor<File> fileResp = resp -> {
            HttpHeaders respHeaders = resp.getHeaders();
            String filename = extractFilenameInUrl(fileUrl, respHeaders.getFirst("Content-Disposition"));
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String filepath = FilenameUtils.concat(path, filename);
            File file = new File(filepath);
            StreamUtils.copy(resp.getBody(), new FileOutputStream(file));
            return file;
        };
        return template.execute(fileUrl, HttpMethod.GET, requestCallback, fileResp);
    }

    /**
     * 设置是否dug模式
     * 默认为false
     *
     * @param dug 是否开启dug
     */
    public static void setDUG(boolean dug) {
        HttpUtils.DUG = dug;
    }

    /**
     * 设置是否抛出业务异常
     *
     * @param throwRcFail 是否抛出response code业务异常
     */
    public static void setThrowRcFail(boolean throwRcFail) {
        HttpUtils.THROW_RC_FAIL = throwRcFail;
    }

    /**
     * 从文件url路径或者Content-Disposition中提取文件名
     *
     * @param fileUrl            url
     * @param contentDisposition 请求头Content-Disposition
     * @return 文件名
     */
    private static String extractFilenameInUrl(String fileUrl, String contentDisposition) {
        int index;
        if (!StringUtils.isEmpty(contentDisposition) && (index = contentDisposition.indexOf("filename=")) != -1) {
            return contentDisposition.substring(index + 9);
        }
        int i = fileUrl.indexOf("?");
        if (i != -1) {
            fileUrl = fileUrl.substring(0, i);
        }
        String[] split = fileUrl.split("/");
        return split[split.length - 1];
    }

    /**
     * closeableHttpClient 简单的请求
     *
     * @param url         请求url
     * @param httpRequest 请求实体
     * @return 报文
     */
    private static String simpleExecute(String url, HttpRequestBase httpRequest) {
        CloseableHttpClient client = HttpClientBuilder.create().build();
        HttpHeaders httpHeaders = getHeader();
        if (!CollectionUtils.isEmpty(httpHeaders)) {
            httpHeaders.forEach((k, v) -> httpRequest.addHeader(k, String.valueOf(v)));
        }
        try {
            CloseableHttpResponse response = client.execute(httpRequest);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            log.error("url->{}请求异常，msg->{}", url, e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static <T> HttpEntity<T> wrapRequest(T requestBody) {
        if (requestBody == null) {
            return null;
        }
        HttpEntity<T> requestEntity;
        if (requestBody instanceof HttpEntity) {
            requestEntity = (HttpEntity<T>) requestBody;
        } else {
            //默认为application/json;charset=utf-8请求
            requestEntity = new HttpEntity<>(requestBody, getHeader());
        }
        return requestEntity;
    }

    private static HttpHeaders getHeader() {
        Map<String, String> curHeaders = currentHeader.get();
        if (curHeaders == null) {
            return defaultHeader;
        } else {
            currentHeader.remove();
            HttpHeaders httpHeaders = new HttpHeaders();
            curHeaders.forEach(httpHeaders::set);
            return httpHeaders;
        }
    }

    /**
     * 根据协议头选择restTemplate
     *
     * @param url 请求url
     * @return restTemplate
     */
    private static RestTemplate getTemplate(String url) {
        return REST_TEMPLATE;
    }
}
