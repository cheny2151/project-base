package com.cheney.javaconfig.security;

import com.cheney.filter.TestFilter;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import java.util.EnumSet;

/**
 * 注册springSecurityChain过滤器
 * 使用mvc时,将WebSecurityConfig加入到servlet容器配置的getRootConfig中。
 * 使用security，Filter在这里配置
 */
public class SecurityWebInitializer extends AbstractSecurityWebApplicationInitializer {

    /**
     * 配置 filter:
     * 配置在SecurityFilterChan之后的过滤器
     */
    @Override
    protected void afterSpringSecurityFilterChain(ServletContext servletContext) {
        servletContext.addFilter("test", new TestFilter())
                .addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "/test");
    }

}
