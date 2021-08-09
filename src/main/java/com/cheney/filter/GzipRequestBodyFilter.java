package com.cheney.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * gzip请求过滤器
 *
 * @author by chenyi
 * @date 2021/8/9
 */
@Component
public class GzipRequestBodyFilter implements Filter {

    private final static String CONTENT_ENCODING_HEADER = "Content-Encoding";
    private final static String HEADER_GZIP_VALUE = "gzip";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String contentEncoding = ((HttpServletRequest) request).getHeader(CONTENT_ENCODING_HEADER);
        if (StringUtils.isNotEmpty(contentEncoding) && contentEncoding.contains(HEADER_GZIP_VALUE)) {
            chain.doFilter(new GzipHttpServletRequestWrapper((HttpServletRequest) request), response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @Slf4j
    static class GzipHttpServletRequestWrapper extends HttpServletRequestWrapper {
        private final HttpServletRequest request;

        public GzipHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
            this.request = request;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            ServletInputStream stream = request.getInputStream();
            final GZIPInputStream gzipInputStream = new GZIPInputStream(stream);
            return new DelegatingServletInputStream(gzipInputStream);
        }
    }

    static class DelegatingServletInputStream extends ServletInputStream {
        private final InputStream sourceStream;
        private boolean finished = false;

        public DelegatingServletInputStream(InputStream sourceStream) {
            this.sourceStream = sourceStream;
        }

        public int read() throws IOException {
            int data = this.sourceStream.read();
            if (data == -1) {
                this.finished = true;
            }
            return data;
        }

        public int available() throws IOException {
            return this.sourceStream.available();
        }

        public void close() throws IOException {
            super.close();
            this.sourceStream.close();
        }

        public boolean isFinished() {
            return this.finished;
        }

        public boolean isReady() {
            return true;
        }

        public void setReadListener(ReadListener readListener) {
            throw new UnsupportedOperationException();
        }
    }

}