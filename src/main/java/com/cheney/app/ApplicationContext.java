package com.cheney.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(scanBasePackages = {"com.cheney.*"})
@EnableTransactionManagement
@MapperScan(value = "com.cheney.dao.mybatis")
public class ApplicationContext {

    public static void main(String[] args) {
        new SpringApplicationBuilder(ApplicationContext.class).web(WebApplicationType.SERVLET).run(args);
    }

}
