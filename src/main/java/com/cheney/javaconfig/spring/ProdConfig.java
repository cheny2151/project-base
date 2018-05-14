package com.cheney.javaconfig.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("prod")
@PropertySource(value = {"classpath:prod/system.properties"})
public class ProdConfig {

}
