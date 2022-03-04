package com.cheney.utils.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.client.ClientHttpRequestInterceptor;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * http client builder support
 *
 * @author by chenyi
 * @date 2021/6/11
 */
public class HttpClientBuilderSupport {

    public static final HttpClientBuilderSupport INSTANCE = new HttpClientBuilderSupport();

    public static final int DEFAULT_MAX_TOTAL = 500;
    public static final int DEFAULT_MAX_PER_ROUTE = DEFAULT_MAX_TOTAL / 5;
    public static final int DEFAULT_VALIDATE_AFTER_INACTIVITY = 2 * 1000;
    public static final int DEFAULT_IDLE_TIMEOUT_SECONDS = 30;
    public static final int DEFAULT_KEEP_ALIVE_DURATION = 30 * 1000;
    public static final int DEFAULT_CONNECT_TIMEOUT = 5 * 1000;
    public static final int DEFAULT_SOCKET_TIMEOUT = 10 * 1000;
    public static final int DEFAULT_CONNECTION_REQUEST_TIMEOUT = 10 * 1000;
    public static final int DEFAULT_RETRY_TIME = 3;

    private Integer maxTotal;
    private Integer maxPerRoute;
    private Integer validateAfterInactivity;
    private Integer idleTimeoutSeconds;
    private Integer connectTimeout;
    private Integer socketTimeout;
    private Integer connectionRequestTimeout;
    private List<ClientHttpRequestInterceptor> interceptors;
    private HttpRequestRetryHandler retryHandler;

    public HttpClientBuilderSupport() {
        this(DEFAULT_MAX_TOTAL, DEFAULT_MAX_PER_ROUTE);
    }

    public HttpClientBuilderSupport(Integer maxTotal, Integer maxPerRoute) {
        this(maxTotal, maxPerRoute, DEFAULT_VALIDATE_AFTER_INACTIVITY, DEFAULT_IDLE_TIMEOUT_SECONDS,
                DEFAULT_CONNECT_TIMEOUT, DEFAULT_SOCKET_TIMEOUT, DEFAULT_CONNECTION_REQUEST_TIMEOUT);
    }

    public HttpClientBuilderSupport(Integer maxTotal, Integer maxPerRoute, Integer validateAfterInactivity, Integer idleTimeoutSeconds,
                                    Integer connectTimeout, Integer socketTimeout, Integer connectionRequestTimeout) {
        maxTotal = maxTotal == null ? DEFAULT_MAX_TOTAL : maxTotal;
        maxPerRoute = maxPerRoute == null ? DEFAULT_MAX_PER_ROUTE : maxPerRoute;
        validateAfterInactivity = validateAfterInactivity == null ? DEFAULT_VALIDATE_AFTER_INACTIVITY : validateAfterInactivity;
        idleTimeoutSeconds = idleTimeoutSeconds == null ? DEFAULT_IDLE_TIMEOUT_SECONDS : idleTimeoutSeconds;
        connectTimeout = connectTimeout == null ? DEFAULT_CONNECT_TIMEOUT : connectTimeout;
        socketTimeout = socketTimeout == null ? DEFAULT_SOCKET_TIMEOUT : socketTimeout;
        connectionRequestTimeout = connectionRequestTimeout == null ? DEFAULT_CONNECTION_REQUEST_TIMEOUT : connectionRequestTimeout;
        this.maxTotal = maxTotal;
        this.maxPerRoute = maxPerRoute;
        this.validateAfterInactivity = validateAfterInactivity;
        this.idleTimeoutSeconds = idleTimeoutSeconds;
        this.connectTimeout = connectTimeout;
        this.socketTimeout = socketTimeout;
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public HttpClientBuilder httpClientBuilder() {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        HttpClientConnectionManager connManager = poolingConnectionManager();
        httpClientBuilder.setConnectionManager(connManager);
        ConnectionKeepAliveStrategy connectionKeepAliveStrategy = new MyConnectionKeepAliveStrategy();
        httpClientBuilder.setKeepAliveStrategy(connectionKeepAliveStrategy);
        if (retryHandler != null) {
            httpClientBuilder.setRetryHandler(retryHandler);
        }
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(socketTimeout)
                .setConnectionRequestTimeout(connectionRequestTimeout) // 从连接池中获取连接最大等待时间
                .build();
        httpClientBuilder.setDefaultRequestConfig(requestConfig);
        return httpClientBuilder;
    }

    /**
     * 链接线程池管理,可以keep-alive不断开链接请求,这样速度会更快 MaxTotal 连接池最大连接数 DefaultMaxPerRoute
     * 每个主机的并发 ValidateAfterInactivity
     * 可用空闲连接过期时间,重用空闲连接时会先检查是否空闲时间超过这个时间，如果超过，释放socket重新建立
     */
    private HttpClientConnectionManager poolingConnectionManager() {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(registry);
        // 设置最大连接数（连接池大小）
        poolingConnectionManager.setMaxTotal(maxTotal);
        // 每个路由的最大连接
        poolingConnectionManager.setDefaultMaxPerRoute(maxPerRoute);
        // 在从连接池获取连接时，连接不活跃多长时间后需要进行一次验证，默认为2s
        poolingConnectionManager.setValidateAfterInactivity(validateAfterInactivity);
        // 关闭一段时间内不活动的连接
        poolingConnectionManager.closeIdleConnections(idleTimeoutSeconds, TimeUnit.SECONDS);
        // 闭失效的连接
        poolingConnectionManager.closeExpiredConnections();
        return poolingConnectionManager;
    }

    /**
     * 如果服务器返回的响应中没有包含Keep-Alive头消息(返回-1)，HttpClient会认为这个连接可以永远保持;
     * 关闭一定时间内不活动的连接，来节省服务器资源
     */
    private static class MyConnectionKeepAliveStrategy extends DefaultConnectionKeepAliveStrategy {

        @Override
        public long getKeepAliveDuration(HttpResponse response, HttpContext context) {
            long keepAliveDuration = super.getKeepAliveDuration(response, context);
            if (keepAliveDuration == -1L) {
                keepAliveDuration = DEFAULT_KEEP_ALIVE_DURATION;
            }
            return keepAliveDuration;
        }

    }

    public Integer getMaxTotal() {
        return maxTotal;
    }

    public void setMaxTotal(Integer maxTotal) {
        this.maxTotal = maxTotal;
    }

    public HttpClientBuilderSupport maxTotal(Integer maxTotal) {
        setMaxTotal(maxTotal);
        return this;
    }

    public Integer getMaxPerRoute() {
        return maxPerRoute;
    }

    public void setMaxPerRoute(Integer maxPerRoute) {
        this.maxPerRoute = maxPerRoute;
    }

    public HttpClientBuilderSupport maxPerRoute(Integer maxPerRoute) {
        setMaxPerRoute(maxPerRoute);
        return this;
    }

    public Integer getValidateAfterInactivity() {
        return validateAfterInactivity;
    }

    public void setValidateAfterInactivity(Integer validateAfterInactivity) {
        this.validateAfterInactivity = validateAfterInactivity;
    }

    public HttpClientBuilderSupport validateAfterInactivity(Integer validateAfterInactivity) {
        setValidateAfterInactivity(validateAfterInactivity);
        return this;
    }

    public Integer getIdleTimeoutSeconds() {
        return idleTimeoutSeconds;
    }

    public void setIdleTimeoutSeconds(Integer idleTimeoutSeconds) {
        this.idleTimeoutSeconds = idleTimeoutSeconds;
    }

    public HttpClientBuilderSupport idleTimeoutSeconds(Integer idleTimeoutSeconds) {
        setIdleTimeoutSeconds(idleTimeoutSeconds);
        return this;
    }

    public Integer getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(Integer connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public HttpClientBuilderSupport connectTimeout(Integer connectTimeout) {
        setConnectTimeout(connectTimeout);
        return this;
    }

    public Integer getSocketTimeout() {
        return socketTimeout;
    }

    public void setSocketTimeout(Integer socketTimeout) {
        this.socketTimeout = socketTimeout;
    }

    public HttpClientBuilderSupport readTimeout(Integer readTimeout) {
        setSocketTimeout(readTimeout);
        return this;
    }

    public Integer getConnectionRequestTimeout() {
        return connectionRequestTimeout;
    }

    public void setConnectionRequestTimeout(Integer connectionRequestTimeout) {
        this.connectionRequestTimeout = connectionRequestTimeout;
    }

    public HttpClientBuilderSupport connectionRequestTimeout(Integer connectionRequestTimeout) {
        setConnectionRequestTimeout(connectionRequestTimeout);
        return this;
    }

    public List<ClientHttpRequestInterceptor> getInterceptors() {
        return interceptors;
    }

    public void setInterceptors(List<ClientHttpRequestInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public HttpClientBuilderSupport interceptors(List<ClientHttpRequestInterceptor> interceptors) {
        setInterceptors(interceptors);
        return this;
    }

    public HttpRequestRetryHandler getRetryHandler() {
        return retryHandler;
    }

    public void setRetryHandler(HttpRequestRetryHandler retryHandler) {
        this.retryHandler = retryHandler;
    }

    public HttpClientBuilderSupport retryHandler(boolean sentRetryEnabled) {
        return retryHandler(DEFAULT_RETRY_TIME, sentRetryEnabled);
    }

    public HttpClientBuilderSupport retryHandler(int retryTime, boolean sentRetryEnabled) {
        if (retryTime > 0) {
            this.retryHandler = new DefaultHttpRequestRetryHandler(retryTime, sentRetryEnabled);
        }
        return this;
    }
}
