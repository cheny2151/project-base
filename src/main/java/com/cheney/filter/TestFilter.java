package com.cheney.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class TestFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println(((HttpServletRequest)request).getRequestURI());
        System.out.println("test filter"+((HttpServletRequest)request).getRequestURI());
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
