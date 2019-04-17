package com.cheney;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan(value = "com.cheney.dao.mybatis")
@ServletComponentScan(basePackages = "com.cheney.filter")
//@EnableApolloConfig
//@RefreshScope
//引入自定义properties
@PropertySource(value = "classpath:test.properties")
public class ApplicationContext {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationContext.class, args);
    }

}
