package com.cheney.javaconfig.spring;

import com.cheney.template.FlushMessageDirective;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.ServletContext;
import java.beans.PropertyVetoException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * spring root配置
 */
@Configuration
@ComponentScan(basePackages = {"com.cheney"}, excludeFilters = {@ComponentScan.Filter({Controller.class})})
@EnableTransactionManagement //启动事务注解
public class RootConfig {

    private final ServletContext servletContext;

    private final FlushMessageDirective flushMessageDirective;

    private final Environment env;

    @Autowired
    public RootConfig(ServletContext servletContext, FlushMessageDirective flushMessageDirective, Environment env) {
        this.servletContext = servletContext;
        this.flushMessageDirective = flushMessageDirective;
        this.env = env;
    }

    /**
     * DataSource配置
     *
     * @return
     */
    @Bean(name = "dataSource")
    public ComboPooledDataSource dataSource() {
        ComboPooledDataSource comboPooledDataSource = new ComboPooledDataSource();
        try {
            comboPooledDataSource.setDriverClass(env.getRequiredProperty("jdbc.driver"));
            comboPooledDataSource.setJdbcUrl(env.getRequiredProperty("jdbc.url"));
            comboPooledDataSource.setUser(env.getRequiredProperty("jdbc.username"));
            comboPooledDataSource.setPassword(env.getRequiredProperty("jdbc.password"));
            comboPooledDataSource.setInitialPoolSize(env.getRequiredProperty("connection_pools.initial_pool_size", int.class));
            comboPooledDataSource.setMinPoolSize(env.getRequiredProperty("connection_pools.min_pool_size", int.class));
            comboPooledDataSource.setMaxPoolSize(env.getRequiredProperty("connection_pools.max_pool_size", int.class));
            comboPooledDataSource.setMaxIdleTime(env.getRequiredProperty("connection_pools.max_idle_time", int.class));
            comboPooledDataSource.setAcquireIncrement(env.getRequiredProperty("connection_pools.acquire_increment", int.class));
            comboPooledDataSource.setCheckoutTimeout(env.getRequiredProperty("connection_pools.checkout_timeout", int.class));
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        return comboPooledDataSource;
    }

    /**
     * freemarker配置
     *
     * @return
     */
    @Bean(name = "freeMarkerConfigurer")
    public FreeMarkerConfig freeMarkerConfigurer() {
        FreeMarkerConfigurer freeMarkerConfigurer = new FreeMarkerConfigurer();
        freeMarkerConfigurer.setTemplateLoaderPath(env.getRequiredProperty("template.loader_path"));
        //freemarker properties
        Properties properties = new Properties();
        properties.setProperty("defaultEncoding", env.getRequiredProperty("template.encoding"));
        properties.setProperty("url_escaping_charset", env.getRequiredProperty("template.encoding"));
        properties.setProperty("template_update_delay", env.getRequiredProperty("template.update_delay"));
        properties.setProperty("tag_syntax", env.getRequiredProperty("template.tag_syntax"));
        properties.setProperty("whitespace_stripping", "true");
        properties.setProperty("classic_compatible", "true");
        properties.setProperty("number_format", env.getRequiredProperty("template.number_format"));
        properties.setProperty("boolean_format", env.getRequiredProperty("template.boolean_format"));
        properties.setProperty("datetime_format", env.getRequiredProperty("template.datetime_format"));
        properties.setProperty("date_format", env.getRequiredProperty("template.date_format"));
        properties.setProperty("time_format", env.getRequiredProperty("template.time_format"));
        properties.setProperty("object_wrapper", "freemarker.ext.beans.BeansWrapper");
        freeMarkerConfigurer.setFreemarkerSettings(properties);
        //freemarker variables
        Map<String, Object> variables = new HashMap<>();
        variables.put("contextPath", servletContext.getContextPath());
        variables.put("time", System.currentTimeMillis());
        variables.put("flush_message", flushMessageDirective);
        freeMarkerConfigurer.setFreemarkerVariables(variables);
        return freeMarkerConfigurer;
    }

    /**
     * 事务配置
     * 加@EnableTransactionManagement注解,并配置一个PlatformTransactionManager的bean
     * dto:JpaTransactionManager
     * jdbc:DataSourceTransactionManager
     * hibernate:HibernateTransactionManager
     */
    @Bean
    public PlatformTransactionManager platformTransactionManager() {
        return new DataSourceTransactionManager(dataSource());
    }

}
