package com.cheney.app;

import com.ctrip.framework.apollo.spring.annotation.EnableApolloConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@ComponentScan(basePackages = {"com.cheney"}, excludeFilters = {@ComponentScan.Filter({Controller.class})})
@EnableTransactionManagement
@MapperScan(value = "com.cheney.dao.mybatis")
@EnableApolloConfig
@RefreshScope
public class ApplicationContext {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ApplicationContext.class).web(WebApplicationType.SERVLET).run(args);
    }

}
