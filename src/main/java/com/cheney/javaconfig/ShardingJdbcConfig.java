package com.cheney.javaconfig;

import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author cheney
 * @date 2020-12-12
 */
@Configuration
public class ShardingJdbcConfig {

    @Resource
    private ShardingSphereDataSource shardingSphereDataSource;

    @PostConstruct
    public void config() {
        System.out.println(shardingSphereDataSource);
    }

}
