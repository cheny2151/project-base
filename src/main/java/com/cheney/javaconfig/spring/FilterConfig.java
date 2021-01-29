package com.cheney.javaconfig.spring;

import com.cheney.filter.JsonWebTokenFilter;
import com.cheney.filter.RequestParamFilter;
import com.cheney.filter.RolePermissionFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * filter配置类
 *
 * @author cheney
 * @date 2019-11-13
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<JsonWebTokenFilter> jsonWebTokenFilterFilterRegistration(JsonWebTokenFilter jsonWebTokenFilter) {
        FilterRegistrationBean<JsonWebTokenFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(jsonWebTokenFilter);
        filterFilterRegistrationBean.setOrder(1);
        return filterFilterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<RolePermissionFilter> RolePermissionFilterRegistration(RolePermissionFilter rolePermissionFilter) {
        FilterRegistrationBean<RolePermissionFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(rolePermissionFilter);
        filterFilterRegistrationBean.setOrder(2);
        return filterFilterRegistrationBean;
    }

    @Bean
    public FilterRegistrationBean<RequestParamFilter> RequestParamFilterRegistration(RequestParamFilter requestParamFilter) {
        FilterRegistrationBean<RequestParamFilter> filterFilterRegistrationBean = new FilterRegistrationBean<>();
        filterFilterRegistrationBean.setFilter(requestParamFilter);
        filterFilterRegistrationBean.setOrder(3);
        return filterFilterRegistrationBean;
    }

}
