package com.cheney.javaconfig.spring;

import com.cheney.system.databind.DateEditor;
import com.cheney.system.databind.StringEditor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 配置profiles
 * 1,xml:配置默认加载的profiles spring.profiles.default
 * 2.javaConfig:rootContext.getEnvironment().setDefaultProfiles("xxx");
 * 3,tomcat启动脚本中配置激活的profiles --> catalina.bar/sh -->添加JVM环境变量 JAVA_OPTS = -Dspring.profiles.active=prod
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

    @Profile("test")
    @Bean(name = "profilesBean")
    public String testBean() {
        return "test";
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

}
