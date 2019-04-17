package com.cheney.javaconfig.spring;

import com.cheney.system.databind.DateEditor;
import com.cheney.system.databind.StringEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * 配置profiles
 * 1,xml:配置默认加载的profiles spring.profiles.default
 * 2.javaConfig:rootContext.getEnvironment().setDefaultProfiles("xxx");
 * 3,tomcat启动脚本中配置激活的profiles --> catalina.bar/sh -->添加JVM环境变量：JAVA_OPTS="-Dspring.profiles.active=test"(windows 加set)
 */
@Configuration
public class BeanConfig {

    @Profile("dev")
    @Bean(name = "profilesBean")
    public String devBean() {
        return "dev";
    }

    @Profile("prod")
    @Bean(name = "profilesBean")
    public String prodBean() {
        return "prod";
    }

    /**
     * 日期数据绑定
     */
    @Bean("dateEditor")
    public DateEditor dateEditor() {
        return new DateEditor();
    }

    /**
     * 字符串数据绑定
     */
    @Bean("stringEditor")
    public StringEditor stringEditor() {
        return new StringEditor();
    }

    /**
     * httpRestTemplate
     */
    @Bean("restTemplate")
    public RestTemplate restTemplate() {
        //httpClient
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setConnectTimeout(5000);
        requestFactory.setReadTimeout(10000);
        return new RestTemplate(requestFactory);
    }

    /**
     * 导出RMI服务
     * 注意RegistryPort端口没被占用
     * 注意接口返回的bean的pack（报全名）应该相同
     */
    /*@Bean
    public RmiServiceExporter httpInvokerUserServiceExporter() {
        RmiServiceExporter exporter = new RmiServiceExporter();
        exporter.setService(new UserServiceImpl());
        exporter.setServiceInterface(com.rmi.UserService.class);
        exporter.setServiceName("UserService");
        exporter.setRegistryPort(8088);
        return exporter;
    }*/

    /**
     * 装配RMI服务
     */
    /*@Bean
    public RmiProxyFactoryBean UserService() {
        RmiProxyFactoryBean proxy = new RmiProxyFactoryBean();
        proxy.setServiceUrl("rmi://localhost:8088/UserService");
        proxy.setServiceInterface(com.rmi.UserService.class);
        return proxy;
    }*/

    /**
     * 导出HttpInvoker
     */
    /*@Bean
    public HttpInvokerServiceExporter userServiceExporter(UserService userService) {
        HttpInvokerServiceExporter exporter = new HttpInvokerServiceExporter();
        exporter.setService(userService);
        exporter.setServiceInterface(UserService.class);
        exporter.setAcceptProxyClasses(true);
        return exporter;
    }*/

    /**
     * 配置HttpInvoker映射
     */
   /* @Bean
    public HandlerMapping httpInvokerMapping() {
        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        Properties properties = new Properties();
        properties.setProperty("/user.service", "userServiceExporter");
        mapping.setMappings(properties);
        mapping.setLazyInitHandlers(false);
        return mapping;
    }*/

}
