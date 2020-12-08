package com.cheney.utils.mybatis.plugin.routing;

import com.cheney.utils.mybatis.plugin.routing.format.RoutingFormatter;

/**
 * 分表路由信息
 *
 * @author cheney
 * @date 2020-11-29
 */
public class RoutingInfo {

    private final Class<?> mapperClass;

    private final String table;

    private final String dependColumn;

    private final RoutingFormatter routingFormatter;

    private final ThreadLocal<String> tail = new ThreadLocal<>();

    public RoutingInfo(Class<?> mapperClass, String table, String dependColumn, RoutingFormatter routingFormatter) {
        this.mapperClass = mapperClass;
        this.table = table;
        this.dependColumn = dependColumn;
        this.routingFormatter = routingFormatter;
    }

    public Class<?> getMapperClass() {
        return mapperClass;
    }

    public String getTable() {
        return table;
    }

    public String getDependColumn() {
        return dependColumn;
    }

    public RoutingFormatter getRoutingFormatter() {
        return routingFormatter;
    }

    public void setCurrentTail(String tail) {
        this.tail.set(tail);
    }

    public String getCurrentTail(){
        return tail.get();
    }
}
