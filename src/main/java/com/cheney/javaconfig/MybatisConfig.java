package com.cheney.javaconfig;

import com.cheney.utils.mybatis.plugin.routing.BandingRoutingInterceptor;
import com.cheney.utils.mybatis.plugin.routing.RoutingSchemaInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author cheney
 * @date 2020-11-29
 */
@Configuration
public class MybatisConfig {

    @Bean
    public BandingRoutingInterceptor bandingRoutingInterceptor() {
        return new BandingRoutingInterceptor();
    }

    @Bean
    public RoutingSchemaInterceptor routingSchemaInterceptor() {
        return new RoutingSchemaInterceptor();
    }

}
