package com.cheney.utils.http;

import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.protocol.HttpContext;

import java.util.concurrent.TimeUnit;

/**
 * http client builder
 *
 * @author by chenyi
 * @date 2021/6/11
 */
public class HttpClientBuilderSupport {

    public static final int DEFAULT_MAX_TOTAL = 300;
    public static final int DEFAULT_MAX_PER_ROUTE = DEFAULT_MAX_TOTAL / 10;
    public static final int DEFAULT_VALIDATE_AFTER_INACTIVITY = 10 * 1000;
    public static final int DEFAULT_IDLE_TIMEOUT_SECONDS = 30;
    public static final int DEFAULT_KEEP_ALIVE_DURATION = 30 * 1000;

    /**
     * 设置HTTP连接管理器,连接池相关配置管理
     *
     * @return 客户端链接管理器
     */
    public static HttpClientBuilder httpClientBuilder() {
        return httpClientBuilder(DEFAULT_MAX_TOTAL, DEFAULT_MAX_PER_ROUTE, DEFAULT_VALIDATE_AFTER_INACTIVITY, DEFAULT_IDLE_TIMEOUT_SECONDS);
    }

    public static HttpClientBuilder httpClientBuilder(int maxTotal, int maxPerRoute, int validateAfterInactivity, int idleTimeoutSeconds) {
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        HttpClientConnectionManager connManager = poolingConnectionManager(maxTotal, maxPerRoute, validateAfterInactivity, idleTimeoutSeconds);
        httpClientBuilder.setConnectionManager(connManager);
        ConnectionKeepAliveStrategy connectionKeepAliveStrategy = new MyConnectionKeepAliveStrategy();
        httpClientBuilder.setKeepAliveStrategy(connectionKeepAliveStrategy);
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(5 * 1000)
                .setSocketTimeout(10 * 1000)
                .setConnectionRequestTimeout(10 * 1000)
                .build();
        httpClientBuilder.setDefaultRequestConfig(requestConfig);
        return httpClientBuilder;
    }

    /**
     * 链接线程池管理,可以keep-alive不断开链接请求,这样速度会更快 MaxTotal 连接池最大连接数 DefaultMaxPerRoute
     * 每个主机的并发 ValidateAfterInactivity
     * 可用空闲连接过期时间,重用空闲连接时会先检查是否空闲时间超过这个时间，如果超过，释放socket重新建立
     */
    private static HttpClientConnectionManager poolingConnectionManager(int maxTotal, int maxPerRoute, int validateAfterInactivity, int idleTimeoutSeconds) {
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();
        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(registry);
        // 设置最大连接数
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
}
