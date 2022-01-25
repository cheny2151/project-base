package com.cheney.utils.http;

import com.cheney.exception.FailHttpStatusResponseException;
import com.cheney.exception.FailRCResponseException;
import com.cheney.system.protocol.BaseResponse;
import com.cheney.system.protocol.ResponseCode;
import com.cheney.utils.JsonUtils;
import com.cheney.utils.URLUtils;
import com.cheney.utils.http.interceptor.GZIPRequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP请求工具类
 */
@Slf4j
public class HttpTool {

    /**
     * 系统临时文件目录
     */
    private final String TMPDIR = System.getProperty("java.io.tmpdir");

    /**
     * 是否显示日志
     */
    private boolean showLog = true;

    /**
     * 是否抛出response code异常
     */
    private boolean throwRcFail = true;

    /**
     * 默认请求头
     */
    private HttpHeaders defaultHeader;

    /**
     * 特殊请求头设置
     */
    private ThreadLocal<Map<String, String>> currentHeader;

    /**
     * rest template
     */
    private final RestTemplate restTemplate;

    /**
     * http client
     */
    private final HttpClient httpClient;

    public HttpTool() {
        this(HttpClientBuilderSupport.INSTANCE, null);
    }

    public HttpTool(HttpHeaders defaultHeader) {
        this(HttpClientBuilderSupport.INSTANCE, defaultHeader);
    }

    public HttpTool(HttpClientBuilderSupport httpClientBuilderSupport) {
        this(httpClientBuilderSupport, null);
    }

    public HttpTool(HttpClientBuilderSupport httpClientBuilderSupport, HttpHeaders defaultHeader) {
        if (defaultHeader == null) {
            defaultHeader = new HttpHeaders();
            defaultHeader.setContentType(MediaType.APPLICATION_JSON);
        }
        this.defaultHeader = defaultHeader;
        this.currentHeader = new ThreadLocal<>();

        Integer connectTimeout = httpClientBuilderSupport.getConnectTimeout();
        Integer connectionRequestTimeout = httpClientBuilderSupport.getConnectionRequestTimeout();
        HttpClientBuilder httpClientBuilder = httpClientBuilderSupport.httpClientBuilder();
        CloseableHttpClient client = httpClientBuilder.build();
        // 初始化template
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(client);
        requestFactory.setConnectTimeout(connectTimeout);
        requestFactory.setReadTimeout(connectionRequestTimeout);
        requestFactory.setConnectionRequestTimeout(connectionRequestTimeout);
        this.httpClient = client;
        RestTemplate template = new RestTemplate(requestFactory);
        this.restTemplate = template;
        // 设置额外拦截器
        List<ClientHttpRequestInterceptor> addInterceptors = httpClientBuilderSupport.getInterceptors();
        List<ClientHttpRequestInterceptor> interceptors = template.getInterceptors();
        interceptors.add(new GZIPRequestInterceptor());
        if (!CollectionUtils.isEmpty(addInterceptors)) {
            interceptors.addAll(addInterceptors);
            template.setInterceptors(interceptors);
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
    public <R> ResponseEntity<R> getForEntity(String url,
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
    public <R> R getForObjectThrowFail(String url,
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
    public <R, B extends BaseResponse<R>> B getForBaseResponse(String url,
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
    public <R, B extends BaseResponse<R>> B getForBaseResponseThrowFail(String url,
                                                                        ParameterizedTypeReference<B> resultType,
                                                                        Object... uriVariables) {
        return forBaseResponseThrowFail(HttpMethod.GET, url, null, resultType, uriVariables);
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
    public <R> ResponseEntity<R> postForEntity(String url, Object requestBody,
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
    public <R> R postForObjectThrowFail(String url, Object requestBody,
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
    public <R, B extends BaseResponse<R>> B postForBaseResponse(String url, Object requestBody,
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
    public <R, B extends BaseResponse<R>> B postForBaseResponseThrowFail(String url, Object requestBody,
                                                                         ParameterizedTypeReference<B> resultType,
                                                                         Object... uriVariables) {
        return forBaseResponseThrowFail(HttpMethod.POST, url, requestBody, resultType, uriVariables);
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
    public <R> ResponseEntity<R> getForEntity(String url, Class<R> resultType, Object... uriVariables) {
        return forEntity(HttpMethod.POST, url, null, resultType, uriVariables);
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
        ResponseEntity<R> response = forEntity(HttpMethod.GET, url, null, resultType, uriVariables);
        return response.getBody();
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
        return forEntity(HttpMethod.POST, url, requestBody, resultType, uriVariables);
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
        ResponseEntity<R> response = forEntity(HttpMethod.POST, url, requestBody, resultType, uriVariables);
        return response.getBody();
    }

    /**
     * http请求，返回包括响应状态的entity
     *
     * @param url          请求地址
     * @param requestBody  请求数据
     * @param resultType   返回泛型类型
     * @param <R>          返回类型
     * @param uriVariables url参数(替换{}占位符)
     * @return 响应体
     */
    public <R> ResponseEntity<R> forEntity(HttpMethod method, String url, Object requestBody,
                                           ParameterizedTypeReference<R> resultType,
                                           Object... uriVariables) {
        return forEntity(method, url, requestBody, (Object) resultType, uriVariables);
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
        return forEntity(method, url, requestBody, (Object) resultType, uriVariables);
    }

    /**
     * post请求，返回包括响应状态的entity
     *
     * @param url          请求地址
     * @param requestBody  请求数据
     * @param resultType   返回泛型类型
     * @param <R>          返回类型
     * @param uriVariables url参数(替换{}占位符)
     * @return 响应体
     */
    public <R> R forObjectThrowFail(HttpMethod method, String url, Object requestBody,
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
    public <R> R forObjectThrowFail(HttpMethod method, String url, Object requestBody,
                                    Class<R> resultType,
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
    public <R, B extends BaseResponse<R>> B forBaseResponse(HttpMethod method, String url, Object requestBody,
                                                            ParameterizedTypeReference<B> resultType,
                                                            Object... uriVariables) {
        ResponseEntity<B> response = forEntity(method, url, requestBody, resultType, uriVariables);
        checkRespStatus(method, url, requestBody, uriVariables, response);
        return response.getBody();
    }

    /**
     * post请求，返回包括响应状态的entity
     * rc不等于200时抛出业务异常
     *
     * @param url          请求地址
     * @param requestBody  请求数据
     * @param resultType   返回泛型类型
     * @param <R>          返回类型
     * @param uriVariables url参数(替换{}占位符)
     * @return 响应体
     */
    public <R, B extends BaseResponse<R>> B forBaseResponseThrowFail(HttpMethod method, String url, Object requestBody,
                                                                     ParameterizedTypeReference<B> resultType,
                                                                     Object... uriVariables) {
        B responseBody = forBaseResponse(method, url, requestBody, resultType, uriVariables);
        if (throwRcFail) {
            if (responseBody == null) {
                throw new FailRCResponseException("url:\"" + url + "\"请求失败,响应体为null", null);
            }
            //业务异常，通过controller通知器直接透传给前端
            Integer code = responseBody.getCode();
            if (code == null || ResponseCode.SUCCESS.getStatus() != code) {
                throw new FailRCResponseException("url:" + url + ", 请求异常, code:" + code + ", msg:" + responseBody.getMsg(),
                        responseBody);
            }
        }
        return responseBody;
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
    @SuppressWarnings("unchecked")
    private <R> ResponseEntity<R> forEntity(HttpMethod method, String url, Object requestBody,
                                            Object resultType, Object... uriVariables) {
        HttpEntity<?> requestEntity = wrapRequest(requestBody);
        try {
            ResponseEntity<R> responseEntity;
            RestTemplate template = getTemplate();
            boolean parameterizedResult = ParameterizedTypeReference.class.isAssignableFrom(resultType.getClass());
            long startTime = System.currentTimeMillis();
            if (uriVariables.length == 1 && uriVariables[0] instanceof Map) {
                Map<String, ?> uriVariableMap = (Map<String, ?>) uriVariables[0];
                String fixUrl = fixUrlVariable(url, uriVariableMap);
                responseEntity = parameterizedResult ?
                        template.exchange(fixUrl, method, requestEntity, (ParameterizedTypeReference<R>) resultType, uriVariableMap) :
                        template.exchange(fixUrl, method, requestEntity, (Class<R>) resultType, uriVariableMap);
            } else {
                responseEntity = parameterizedResult ?
                        template.exchange(url, method, requestEntity, (ParameterizedTypeReference<R>) resultType, uriVariables) :
                        template.exchange(url, method, requestEntity, (Class<R>) resultType, uriVariables);
            }
            if (showLog) {
                long useTime = System.currentTimeMillis() - startTime;
                Object resp = responseEntity.getBody();
                if (uriVariables.length > 0) {
                    log.info("useTime:{}ms, reqUrl:{}, requestBody:{}, uriVariables:{}, responseBody:{}",
                            useTime, url, JsonUtils.toJson(uriVariables), JsonUtils.toJson(requestBody), LogUtils.cutLog(resp));
                } else {
                    log.info("useTime:{}ms, reqUrl:{}, requestBody:{}, responseBody:{}",
                            useTime, url, JsonUtils.toJson(requestBody), LogUtils.cutLog(resp));
                }
            }
            return responseEntity;
        } catch (Exception e) {
            log.error("reqUrl:{}请求异常,requestBody:{},uriVariables:{}，msg:{}",
                    url, JsonUtils.toJson(requestBody), uriVariables, e.getMessage());
            throw e;
        }
    }

    /**
     * 发送简单的post请求
     *
     * @param url 请求url
     */
    public String simpleGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        return simpleExecute(url, httpGet);
    }

    /**
     * 发送简单的post请求
     *
     * @param url         请求url
     * @param requestBody 请求数据
     * @param contentType content-type
     */
    public String simplePost(String url, Object requestBody, ContentType contentType) {
        HttpPost httpPost = new HttpPost(url);
        StringEntity stringEntity = new StringEntity(JsonUtils.toJson(requestBody), contentType);
        httpPost.setEntity(stringEntity);
        return simpleExecute(url, httpPost);
    }

    /**
     * 简单的请求,返回字符串
     *
     * @param url         请求url
     * @param httpRequest 请求实体
     * @return 报文
     */
    public String simpleExecute(String url, HttpRequestBase httpRequest) {
        HttpHeaders httpHeaders = getHeader();
        if (!CollectionUtils.isEmpty(httpHeaders)) {
            httpHeaders.forEach((k, v) -> httpRequest.addHeader(k, String.valueOf(v)));
        }
        try {
            HttpResponse response = httpClient.execute(httpRequest);
            return EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            log.error("url->{}请求异常，msg->{}", url, e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 下载文件
     *
     * @param fileUrl 目标文件地址
     * @return 文件
     */
    public File downloadFile(String fileUrl) {
        return downloadFile(fileUrl, TMPDIR);
    }

    /**
     * 下载文件
     *
     * @param fileUrl 目标文件地址
     * @param path    下载目录
     * @return 文件
     */
    public File downloadFile(String fileUrl, String path) {
        RestTemplate template = getTemplate();
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
     * 设置自定义请求头
     * 注意:设置的请求头只能使用一次，被使用后（发送一次http请求）移除自定义请求头
     *
     * @param headers 请求头
     */
    public HttpTool currentHeader(Map<String, String> headers) {
        if (headers == null)
            throw new NullPointerException();
        currentHeader.set(headers);
        return this;
    }

    /**
     * 添加自定义请求头
     * 注意:设置的请求头只能使用一次，被使用后（发送一次http请求）移除自定义请求头
     *
     * @param key   请求头key
     * @param value 请求头value
     */
    public HttpTool addHeader(String key, String value) {
        if (key != null && value != null) {
            Map<String, String> headers = currentHeader.get();
            if (headers == null) {
                headers = new HashMap<>();
                currentHeader.set(headers);
            }
            headers.put(key, value);
        }
        return this;
    }

    /**
     * 设置请求头content type
     * 注意:设置的请求头只能使用一次，被使用后（发送一次http请求）移除自定义请求头
     *
     * @param contentType 请求头content type
     */
    public HttpTool contentType(String contentType) {
        return addHeader("Content-Type", contentType);
    }

    /**
     * 设置是否打印请求日志
     * 默认为false
     *
     * @param showLog 是否打印请求日志
     */
    public HttpTool showLog(boolean showLog) {
        this.showLog = showLog;
        return this;
    }

    /**
     * 设置是否抛出业务异常
     *
     * @param throwRcFail 是否抛出response code业务异常
     */
    public HttpTool throwRcFail(boolean throwRcFail) {
        this.throwRcFail = throwRcFail;
        return this;
    }

    /**
     * 设置默认请求头
     *
     * @param defaultHeader 默认请求头
     */
    public HttpTool defaultHeader(HttpHeaders defaultHeader) {
        this.defaultHeader = defaultHeader;
        return this;
    }

    public HttpHeaders getDefaultHeader() {
        return defaultHeader;
    }

    public RestTemplate getTemplate() {
        return restTemplate;
    }

    public HttpClient getClient() {
        return httpClient;
    }

    /**
     * 校验http响应状态码
     */
    private <R> void checkRespStatus(HttpMethod method, String url, Object requestBody,
                                     Object[] uriVariables, ResponseEntity<R> response) {
        int statusCode = response.getStatusCodeValue();
        //http请求异常
        if (statusCode < 200 || statusCode > 299) {
            Object body = response.getBody();
            String resp = body == null ? "" : JsonUtils.toJson(body);
            log.error("[{}]reqUrl:{},requestBody:{},uriVariables:{},HttpStatus:{},response:{}"
                    , method.name(), url,
                    JsonUtils.toJson(requestBody), JsonUtils.toJson(uriVariables),
                    statusCode, resp);
            throw new FailHttpStatusResponseException(resp, statusCode);
        }
    }

    /**
     * 从文件url路径或者Content-Disposition中提取文件名
     *
     * @param fileUrl            url
     * @param contentDisposition 请求头Content-Disposition
     * @return 文件名
     */
    private String extractFilenameInUrl(String fileUrl, String contentDisposition) {
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
     * 补充参数名指url参数
     *
     * @param url            原url
     * @param uriVariableMap url参数map
     * @return url
     */
    private String fixUrlVariable(String url, Map<String, ?> uriVariableMap) {
        Map<String, String> paramFromURL = URLUtils.getParamFromURL(url);
        for (String key : uriVariableMap.keySet()) {
            if (!paramFromURL.containsKey(key)) {
                url = URLUtils.addRestTemplateParam(url, key);
            }
        }
        return url;
    }

    private <T> HttpEntity<T> wrapRequest(T requestBody) {
        if (requestBody == null) {
            return new HttpEntity<>(null, getHeader());
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

    private HttpHeaders getHeader() {
        Map<String, String> curHeaders = currentHeader.get();
        if (curHeaders == null) {
            return defaultHeader;
        } else {
            currentHeader.remove();
            HttpHeaders httpHeaders = new HttpHeaders(defaultHeader);
            curHeaders.forEach(httpHeaders::set);
            return httpHeaders;
        }
    }

}
