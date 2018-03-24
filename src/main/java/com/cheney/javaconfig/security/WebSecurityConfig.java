package com.cheney.javaconfig.security;

import com.cheney.filter.JsonWebTokenFilter;
import com.cheney.service.security.UserDetailsServiceImpl;
import com.cheney.system.security.JsonAccessDeniedHandler;
import com.cheney.system.security.JsonAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security配置
 * http://blog.csdn.net/carrie__yang/article/details/72867962
 */
@Configuration
//启用web安全性
@EnableWebSecurity
@PropertySource(value = {"classpath:security.properties"})
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Autowired
    public WebSecurityConfig(UserDetailsServiceImpl tokenUserDetailsService) {
        this.userDetailsService = tokenUserDetailsService;
    }

    /**
     * 通过重载,配置user-detail服务(用户储存)
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider());
    }

    /**
     *
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
    }

    /**
     * 通过重载,配置如何通过拦截器保护请求
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //过滤规则
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/blogger/**").hasRole("BLOGGER")
                .anyRequest().permitAll()
                .and().csrf().disable()                                 //禁用spring跨域处理
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS); //spring 永不创建session
        //添加自定义过滤器
        http.addFilterBefore(jsonWebTokenFilter(), UsernamePasswordAuthenticationFilter.class);
        //自定义异常处理
        http.exceptionHandling().accessDeniedHandler(jsonAccessDeniedHandler()).authenticationEntryPoint(jsonAuthenticationEntryPoint());
    }

    /**
     * 显示的声明AuthenticationManager于spring容器
     */
    @Bean("authenticationManager")
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    /**
     * 配置AuthenticationProvider
     * HideUserNotFoundExceptions为false使UserNotFoundExceptions抛出，
     * true则会转为BadCredentialsException
     */
    @Bean("daoAuthenticationProvider")
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(bCryptPasswordEncoder());
        provider.setUserDetailsService(userDetailsService);
        provider.setHideUserNotFoundExceptions(false);
        return provider;
    }

    /**
     * 加密算法:BCrypt
     */
    @Bean("passwordEncoder")
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 自定义token认证filter
     */
    @Bean("jsonWebTokenFilter")
    public JsonWebTokenFilter jsonWebTokenFilter() {
        return new JsonWebTokenFilter();
    }

    /**
     * 403 权限不合法处理类
     */
    @Bean("jsonAccessDeniedHandler")
    public JsonAccessDeniedHandler jsonAccessDeniedHandler() {
        return new JsonAccessDeniedHandler();
    }

    /**
     * 401 未登录处理类
     */
    @Bean("jsonAuthenticationEntryPoint")
    public AuthenticationEntryPoint jsonAuthenticationEntryPoint() {
        return new JsonAuthenticationEntryPoint();
    }

}
