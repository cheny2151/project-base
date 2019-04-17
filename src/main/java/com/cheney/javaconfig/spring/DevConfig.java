package com.cheney.javaconfig.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Profile("dev")
@PropertySource(value = {"classpath:dev/system.properties"})
public class DevConfig {


}
