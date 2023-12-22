package com.cheney.javaconfig.spring;

import com.cheney.utils.httpclient.HttpClientBuilderSupport;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * @author by chenyi
 * @date 2023/12/22
 */
@Configuration
public class RestConfig {

    @Value("${rest.http.connect-timeout:0}")
    private Integer connectTimeout;

    @Value("${rest.http.connection-request-timeout:0}")
    private Integer connectionRequestTimeout;

    @Value("${rest.http.read-timeout:0}")
    private Integer readTimeout;

    @Bean
    public RestTemplate CustomRestTemplate(RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder.requestFactory(this::httpRequestFactory).build();
    }

    public HttpComponentsClientHttpRequestFactory httpRequestFactory() {
        HttpClientBuilderSupport support = new HttpClientBuilderSupport();
        if (connectTimeout != 0) {
            support.setConnectTimeout(connectTimeout);
        }
        if (connectionRequestTimeout != 0) {
            support.setConnectionRequestTimeout(connectionRequestTimeout);
        }
        if (readTimeout != 0) {
            support.setSocketTimeout(readTimeout);
        }
        CloseableHttpClient client = support.httpClientBuilder().build();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory(client);
        factory.setConnectTimeout(support.getConnectTimeout());
        factory.setConnectionRequestTimeout(support.getConnectionRequestTimeout());
        factory.setReadTimeout(support.getSocketTimeout());
        return factory;
    }

}
