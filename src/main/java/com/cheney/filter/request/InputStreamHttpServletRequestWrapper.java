package com.cheney.filter.request;

import com.alibaba.fastjson.JSON;
import com.cheney.utils.RequestParamHolder;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 包装请求，重新写入流
 */
public class InputStreamHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final byte[] body;

    private final ServletInputStream inputStream;

    public InputStreamHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.body = JSON.toJSONString(RequestParamHolder.request()).getBytes(StandardCharsets.UTF_8);
        this.inputStream = new BodyServletInputStream();
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return inputStream;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(inputStream));
    }


    public class BodyServletInputStream extends ServletInputStream {

        private final ByteArrayInputStream inputStream = new ByteArrayInputStream(body);

        @Override
        public boolean isFinished() {
            return inputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }

        @Override
        public int read() throws IOException {
            return inputStream.read();
        }
    }

}
