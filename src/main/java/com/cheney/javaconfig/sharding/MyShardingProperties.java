package com.cheney.javaconfig.sharding;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author cheney
 * @date 2020-12-23
 */
@ConfigurationProperties(prefix = "my.sharding")
@Data
public class MyShardingProperties {

    private String dbname;

    private String tableName;

    private String startDate;

}
