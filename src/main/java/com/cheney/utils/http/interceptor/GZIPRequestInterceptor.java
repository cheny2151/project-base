package com.cheney.utils.http.interceptor;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

/**
 * gzip压缩拦截器
 *
 * @author by chenyi
 * @date 2021/11/4
 */
public class GZIPRequestInterceptor implements ClientHttpRequestInterceptor {

    private final static String CONTENT_ENCODING_HEADER = "Content-Encoding";
    private final static String HEADER_GZIP_VALUE = "gzip";

    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution execution) throws IOException {
        String contentEncoding = httpRequest.getHeaders().getFirst(CONTENT_ENCODING_HEADER);
        if (HEADER_GZIP_VALUE.equals(contentEncoding)) {
            bytes = getGzip(bytes);
        }
        return execution.execute(httpRequest, bytes);
    }

    private byte[] getGzip(byte[] body) throws IOException {

        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        try {
            try (GZIPOutputStream zipStream = new GZIPOutputStream(byteStream)) {
                zipStream.write(body);
            }
        } finally {
            byteStream.close();
        }

        return byteStream.toByteArray();

    }
}
