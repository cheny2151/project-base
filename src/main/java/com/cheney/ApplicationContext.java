package com.cheney;

import com.cheney.javaconfig.MyBeanRegistrar;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@MapperScan(value = "com.cheney.dao.mybatis")
@ServletComponentScan(basePackages = "com.cheney.filter")
@Import({MyBeanRegistrar.class})
//@EnableApolloConfig
//@RefreshScope
public class ApplicationContext {

    public static void main(String[] args) {
        SpringApplication.run(ApplicationContext.class, args);
    }

}
