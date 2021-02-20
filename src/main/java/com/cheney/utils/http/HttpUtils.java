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
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
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
     * 默认请求头
     */
    private static final HttpHeaders defaultHeader;

    /**
     * 特殊请求头设置
     */
    private static ThreadLocal<HttpHeaders> currentHeader;

    /**
     * http请求template
     */
    private final static RestTemplate REST_TEMPLATE;

    /**
     * http+ssl请求template
     */
    private final static RestTemplate HTTPS_REST_TEMPLATE;

    static {
        defaultHeader = new HttpHeaders();
        defaultHeader.setContentType(MediaType.APPLICATION_JSON);

        currentHeader = new ThreadLocal<>();

        // 初始化template
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(10000);
        REST_TEMPLATE = new RestTemplate(requestFactory);

        HttpsClientRequestFactory sslRequestFactory = new HttpsClientRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(10000);
        HTTPS_REST_TEMPLATE = new RestTemplate(sslRequestFactory);
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
                                                                               Object... uriVariables) throws FailRCResponseException {
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
        checkRespStatus(HttpMethod.POST, url, requestBody, uriVariables, response);
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
        BaseResponse<R> responseBody = response.getBody();
        checkRespStatus(HttpMethod.POST, url, requestBody, uriVariables, response);
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

    public static void setCurrentHeader(Map<String, String> headers) {
        if (headers == null)
            throw new NullPointerException();
        HttpHeaders httpHeaders = new HttpHeaders();
        if (!headers.containsKey(HttpHeaders.CONTENT_TYPE)) {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        }
        headers.forEach(httpHeaders::set);
        currentHeader.set(httpHeaders);
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
     * @throws IOException
     */
    public static File downloadFile(String fileUrl) throws IOException {
        return downloadFile(fileUrl, TMPDIR);
    }

    /**
     * 下载文件
     *
     * @param fileUrl 目标文件地址
     * @param path    下载目录
     * @return 文件
     * @throws IOException
     */
    public static File downloadFile(String fileUrl, String path) throws IOException {
        URL url = new URL(fileUrl);
        URLConnection urlConnection = url.openConnection();

        // 提取文件名
        String filename = extractFilenameInUrl(fileUrl, urlConnection.getHeaderField("Content-Disposition"));
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String filepath = FilenameUtils.concat(path, filename);
        File file = new File(filepath);

        // 获取响应流
        InputStream inputStream = urlConnection.getInputStream();
        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] data = new byte[1024];
        int length = data.length;
        int len;
        while ((len = bufferedInputStream.read(data, 0, length)) > 0) {
            fileOutputStream.write(data, 0, len);
            fileOutputStream.flush();
        }
        fileOutputStream.close();
        inputStream.close();
        return file;
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
        HttpHeaders httpHeaders = currentHeader.get();
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
        HttpHeaders httpHeaders = currentHeader.get();
        if (httpHeaders == null) {
            httpHeaders = defaultHeader;
        } else {
            currentHeader.remove();
        }
        return httpHeaders;
    }

    /**
     * 根据协议头选择restTemplate
     *
     * @param url 请求url
     * @return restTemplate
     */
    private static RestTemplate getTemplate(String url) {
        RestTemplate restTemplate = REST_TEMPLATE;
        if (url.substring(0, 5).equalsIgnoreCase("https")) {
            restTemplate = HTTPS_REST_TEMPLATE;
        }
        return restTemplate;
    }
}
