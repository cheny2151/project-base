package com.cheney.utils;

import com.google.common.collect.Range;
import org.apache.shardingsphere.sharding.api.sharding.standard.PreciseShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.RangeShardingValue;
import org.apache.shardingsphere.sharding.api.sharding.standard.StandardShardingAlgorithm;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;


/**
 * @author cheney
 * @date 2020-12-08
 */
public class YearMonthShardingAlgorithm implements StandardShardingAlgorithm<Comparable<?>> {

    private Properties props = new Properties();

    public YearMonthShardingAlgorithm() {
        System.out.println("load");
    }

    @Override
    public String doSharding(Collection<String> collection, PreciseShardingValue<Comparable<?>> preciseShardingValue) {
        return preciseShardingValue.getLogicTableName() + preciseShardingValue.getValue();
    }

    @Override
    public Collection<String> doSharding(Collection<String> collection, RangeShardingValue<Comparable<?>> rangeShardingValue) {
        Range<Comparable<?>> valueRange = rangeShardingValue.getValueRange();
        Date lowerTime;
        Date upperTime;
        if (valueRange.hasLowerBound()) {
            Object lowerEndpoint = valueRange.lowerEndpoint();
            lowerTime = DateUtils.toDate(lowerEndpoint);
        } else {
            String minTableName = collection.stream().min(String::compareTo).orElseThrow();
            String minTime = minTableName.substring(minTableName.lastIndexOf("_") + 1);
            lowerTime = DateUtils.toDate(minTime);
        }
        if (valueRange.hasUpperBound()) {
            Object upperEndpoint = valueRange.upperEndpoint();
            upperTime = DateUtils.toDate(upperEndpoint);
        } else {
            String maxTableName = collection.stream().max(String::compareTo).orElseThrow();
            String maxTime = maxTableName.substring(maxTableName.lastIndexOf("_") + 1);
            upperTime = DateUtils.toDate(maxTime);
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        ArrayList<String> tableNames = new ArrayList<>();
        while (lowerTime.compareTo(upperTime) <= 0) {
            String tableName = rangeShardingValue.getLogicTableName() + "_" + format.format(lowerTime);
            if (collection.contains(tableName)) {
                tableNames.add(tableName);
            }
            lowerTime = org.apache.commons.lang3.time.DateUtils.addMonths(lowerTime, 1);
        }
        System.out.println(tableNames);
        return tableNames;
    }

    @Override
    public void init() {
        System.out.println("init");
    }

    @Override
    public String getType() {
        return "year-month";
    }

    @Override
    public Properties getProps() {
        return null;
    }

    @Override
    public void setProps(Properties properties) {

    }
}
