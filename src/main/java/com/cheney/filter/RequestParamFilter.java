package com.cheney.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cheney.exception.JsonParseException;
import com.cheney.filter.request.InputStreamHttpServletRequestWrapper;
import com.cheney.system.page.PageInfo;
import com.cheney.system.protocol.BaseRequest;
import com.cheney.utils.HttpSupport;
import com.cheney.utils.RequestParamHolder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

import static org.springframework.beans.support.PagedListHolder.DEFAULT_PAGE_SIZE;

/**
 * 使用webFilter必须在启动类上加@ServletComponentScan
 * filter根据类全名自然排序
 */
@Slf4j
@Component
public class RequestParamFilter extends OncePerRequestFilter {

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
        switch (method.toUpperCase()) {
            case HttpSupport.Method.HTTP_METHOD_GET: {
                setRequestParamForUrl(httpServletRequest);
                break;
            }
            case HttpSupport.Method.HTTP_METHOD_POST:
            case HttpSupport.Method.HTTP_METHOD_PUT:
            case HttpSupport.Method.HTTP_METHOD_DELETE: {
                String contentType = httpServletRequest.getContentType();
                if (StringUtils.isEmpty(contentType) || !contentType.toLowerCase().startsWith("application/json")) {
                    break;
                }
                setRequestParamForBody(httpServletRequest);
                //包装请求将消费的流设置回去
                httpServletRequest = new InputStreamHttpServletRequestWrapper(httpServletRequest);
                break;
            }
            default: {
                log.warn("unSupport Http Request Method");
            }
        }
        if (RequestParamHolder.currentRequestParam() != null) {
            log.info("请求url->{},请求RequestParam->{}", requestURI,
                    JSON.toJSONString(RequestParamHolder.currentRequestParam()));
        } else {
            log.info("请求url->{}", requestURI);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
        RequestParamHolder.remove();
    }

    private void setRequestParamForUrl(HttpServletRequest request) {
        BaseRequest<JSONObject> param = new BaseRequest<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        final JSONObject data = new JSONObject();
        param.setData(data);
        parameterMap.forEach((k, v) -> {
            switch (k) {
                //设置基础参数
                case "requestId": {
                    param.setRequestId(v[0]);
                    break;
                }
                case "timestamp": {
                    param.setTimestamp(v[0]);
                    break;
                }
                //设置分页
                case "currentPage": {
                    int currentPage = StringUtils.isNotEmpty(v[0]) && StringUtils.isNumeric(v[0])
                            ? Integer.parseInt(v[0]) : PageInfo.DEFAULT_PAGE_NUMBER;
                    param.initPageable().setPageNumber(currentPage);
                    break;
                }
                case "pageSize": {
                    int pageSize = StringUtils.isNotEmpty(v[0]) && StringUtils.isNumeric(v[0])
                            ? Integer.parseInt(v[0]) : DEFAULT_PAGE_SIZE;
                    param.initPageable().setPageSize(pageSize);
                    break;
                }
                //设置业务参数
                default: {
                    if (v.length == 1)
                        data.put(k, v[0]);
                    else
                        data.put(k, v);
                }
            }
        });

        RequestParamHolder.setRequestParam(param);

    }

    private void setRequestParamForBody(HttpServletRequest request) {
        try {
            ServletInputStream inputStream = request.getInputStream();
            BaseRequest<JSONObject> requestParam = JSON.parseObject(inputStream, BaseRequest.class);
            if (requestParam == null) {
                requestParam = new BaseRequest<>();
            } else if (requestParam.getData() == null) {
                //防止空指针
                requestParam.setData(new JSONObject());
            }
            RequestParamHolder.setRequestParam(requestParam);
        } catch (Exception e) {
            log.error("请求体解析失败", e);
            throw new JsonParseException("请求体解析异常");
        }
    }

}
