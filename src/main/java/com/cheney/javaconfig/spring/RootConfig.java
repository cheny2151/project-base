package com.cheney.javaconfig.spring;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.cheney.template.FlushMessageDirective;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.persistence.criteria.CriteriaBuilder;
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
@PropertySource(value = {"classpath:system.properties"})
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
    @Bean(name = "dataSource", destroyMethod = "close")
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
     * jpa配置
     *
     * @return
     */
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(dataSource());
        entityManagerFactory.setPackagesToScan("com.cheney.entity");
        entityManagerFactory.setPersistenceUnitName("persistenceUnit");
        //jpa vendor
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(env.getProperty("hibernate.show_sql",boolean.class));
        vendorAdapter.setGenerateDdl(env.getProperty("hibernate.generate_ddl",boolean.class));
        entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
        //jpa Properties
        Properties jpaPro = new Properties();
        jpaPro.setProperty("hibernate.dialect", env.getRequiredProperty("hibernate.dialect"));
        jpaPro.setProperty("hibernate.ejb.naming_strategy", env.getRequiredProperty("hibernate.ejb.naming_strategy"));
        jpaPro.setProperty("hibernate.cache.use_second_level_cache", env.getRequiredProperty("hibernate.cache.use_second_level_cache"));
        jpaPro.setProperty("hibernate.cache.region.factory_class", env.getRequiredProperty("hibernate.cache.region.factory_class"));
        jpaPro.setProperty("hibernate.cache.use_query_cache", env.getRequiredProperty("hibernate.cache.use_query_cache"));
        jpaPro.setProperty("hibernate.jdbc.fetch_size", env.getRequiredProperty("hibernate.jdbc.fetch_size"));
        jpaPro.setProperty("hibernate.jdbc.batch_size", env.getRequiredProperty("hibernate.jdbc.batch_size"));
        jpaPro.setProperty("hibernate.connection.isolation", env.getRequiredProperty("hibernate.connection.isolation"));
        jpaPro.setProperty("javax.persistence.validation.mode", env.getRequiredProperty("javax.persistence.validation.mode"));
        entityManagerFactory.setJpaProperties(jpaPro);
        return entityManagerFactory;
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
     * jpa:JpaTransactionManager
     * jdbc:DataSourceTransactionManager
     * hibernate:HibernateTransactionManager
     */
    @Bean
    public PlatformTransactionManager platformTransactionManager() {
        return new JpaTransactionManager(entityManagerFactory().getObject());
    }

}
