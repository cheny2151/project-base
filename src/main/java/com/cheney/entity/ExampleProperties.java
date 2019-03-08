package com.cheney.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 用于Apollo配置热刷新的例子 详见@PropertiesListener
 *
 * @author cheney
 */
@Component("exampleProperties")
@ConfigurationProperties(prefix = "example")
@RefreshScope
@Data
public class ExampleProperties {

    private Map<String, String> properties;

}
