package com.cheney.javaconfig.sharding;

import com.cheney.utils.DateUtils;
import com.cheney.utils.ReflectUtils;
import org.apache.shardingsphere.driver.jdbc.core.datasource.ShardingSphereDataSource;
import org.apache.shardingsphere.infra.context.schema.SchemaContexts;
import org.apache.shardingsphere.infra.datanode.DataNode;
import org.apache.shardingsphere.sharding.rule.ShardingRule;
import org.apache.shardingsphere.sharding.rule.TableRule;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author cheney
 * @date 2020-12-12
 */
@Configuration
public class ShardingJdbcConfig {

    @Resource
    private ShardingSphereDataSource shardingSphereDataSource;

    @Resource
    private MyShardingProperties myShardingProperties;

    @PostConstruct
    public void config() throws IllegalAccessException {
        String startDate = myShardingProperties.getStartDate();
        String tableName = myShardingProperties.getTableName();
        String dbname = myShardingProperties.getDbname();

        SchemaContexts schemaContexts = shardingSphereDataSource.getSchemaContexts();
        ShardingRule sphereRule = (ShardingRule) schemaContexts.getSchemas().get("logic_db").getRules().stream().findFirst().orElseThrow();
        TableRule tableRule = sphereRule.getTableRule(tableName);
        if (tableRule != null) {
            Date date = DateUtils.toDate(startDate);
            Date now = new Date();
            List<String> actualTableNames = new ArrayList<>();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMM");
            while (date.compareTo(now) < 0) {
                actualTableNames.add(tableName + "_" + dateFormat.format(date));
                date = org.apache.commons.lang3.time.DateUtils.addMonths(date, 1);
            }
            List<DataNode> dataNodes =
                    (List<DataNode>) ReflectUtils.field(tableRule.getClass(), "actualDataNodes").get(tableRule);
            dataNodes.clear();
            for (String actualTableName : actualTableNames) {
                dataNodes.add(new DataNode(dbname, actualTableName));
            }
            Set<String> actualTables =
                    (Set<String>) ReflectUtils.field(tableRule.getClass(), "actualTables").get(tableRule);
            actualTables.clear();
            actualTables.addAll(actualTableNames);
            Map<DataNode, Integer> dataNodeIndexMap =
                    (Map<DataNode, Integer>) ReflectUtils.field(tableRule.getClass(), "dataNodeIndexMap").get(tableRule);
            dataNodeIndexMap.clear();
            for (int i = 0; i < dataNodes.size(); i++) {
                dataNodeIndexMap.put(dataNodes.get(i), i);
            }
            Map<String, Collection<String>> datasourceToTablesMap =
                    (Map<String, Collection<String>>) ReflectUtils.field(tableRule.getClass(), "datasourceToTablesMap").get(tableRule);
            datasourceToTablesMap.put(dbname, actualTables);
        }
    }

}
