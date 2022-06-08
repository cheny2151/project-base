package com.cheney.filter;

import cn.cheny.toolbox.other.map.EasyMap;
import com.alibaba.fastjson.JSON;
import com.cheney.exception.JsonParseException;
import com.cheney.filter.request.InputStreamHttpServletRequestWrapper;
import com.cheney.system.protocol.BaseResponse;
import com.cheney.utils.HttpSupport;
import com.cheney.utils.http.RequestParamHolderWithoutBaseRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 使用webFilter必须在启动类上加@ServletComponentScan
 * filter根据类全名自然排序
 */
@Slf4j
@Component
public class RequestParamFilterWithoutBaseRequest extends OncePerRequestFilter {

    // 忽略的url正则表达式
    private final static String[] IGNORE_PATTERN = new String[]{".*.ico", ".*.html"};

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String requestURI = httpServletRequest.getRequestURI();
        // 放行忽略的url
        for (String ignore : IGNORE_PATTERN) {
            if (requestURI.matches(ignore)) {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }
        }
        String method = httpServletRequest.getMethod();
        String reqStr = null;
        switch (method.toUpperCase()) {
            case HttpSupport.Method.HTTP_METHOD_GET: {
                reqStr = setRequestParamForUrl(httpServletRequest);
                break;
            }
            case HttpSupport.Method.HTTP_METHOD_POST:
            case HttpSupport.Method.HTTP_METHOD_PUT:
            case HttpSupport.Method.HTTP_METHOD_PATCH:
            case HttpSupport.Method.HTTP_METHOD_DELETE: {
                String contentType = httpServletRequest.getContentType();
                if (StringUtils.isEmpty(contentType) || !contentType.toLowerCase().startsWith("application/json")) {
                    break;
                }
                try {
                    reqStr = setRequestParamForBody(httpServletRequest);
                } catch (JsonParseException e) {
                    BaseResponse.error(e.getMessage()).writeToResponse(httpServletResponse);
                    return;
                }
                //包装请求将消费的流设置回去
                httpServletRequest = new InputStreamHttpServletRequestWrapper(httpServletRequest);
                break;
            }
            // 放行忽略的method
            case HttpSupport.Method.HTTP_METHOD_OPTIONS: {
                filterChain.doFilter(httpServletRequest, httpServletResponse);
                return;
            }
            default: {
                log.warn("un support Http Request Method");
            }
        }
        try {
            RequestParamHolderWithoutBaseRequest.setCurrentRequest(httpServletRequest);
            RequestParamHolderWithoutBaseRequest.setCurrentResponse(httpServletResponse);
            RequestParamHolderWithoutBaseRequest.generateInnerId();
            String innerId = RequestParamHolderWithoutBaseRequest.getInnerId();
            MDC.put("INNER_ID", innerId);
            if (reqStr != null) {
                log.info("[{}]url->[{}],request param:{}", method, requestURI, reqStr);
            } else {
                log.info("[{}]url->[{}]", method, requestURI);
            }
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            RequestParamHolderWithoutBaseRequest.remove();
            MDC.clear();
        }
    }

    private String setRequestParamForUrl(HttpServletRequest request) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        final EasyMap data = new EasyMap();
        parameterMap.forEach((k, v) -> {
            //设置业务参数
            if (v.length == 1)
                data.put(k, v[0]);
            else
                data.put(k, v);
        });

        RequestParamHolderWithoutBaseRequest.setRequestParam(data);
        return JSON.toJSONString(parameterMap);
    }

    private String setRequestParamForBody(HttpServletRequest request) {
        try {
            ServletInputStream inputStream = request.getInputStream();
            ByteArrayOutputStream byteArray = new ByteArrayOutputStream();
            byte[] temp = new byte[1024];
            int len;
            while ((len = inputStream.read(temp)) != -1) {
                byteArray.write(temp, 0, len);
            }
            String json = byteArray.toString(String.valueOf(StandardCharsets.UTF_8));
            EasyMap requestParam = JSON.parseObject(json, EasyMap.class);
            if (requestParam == null) {
                requestParam = new EasyMap();
            }
            RequestParamHolderWithoutBaseRequest.setRequestParam(requestParam);
            return json;
        } catch (Exception e) {
            log.error("请求体解析失败:{}", e.getMessage());
            throw new JsonParseException("Invalid Request Body");
        }
    }

}
