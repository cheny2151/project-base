package com.cheney.utils.mybatis.plugin.routing;

import com.cheney.utils.mybatis.plugin.routing.format.MonthRoutingFormatter;
import com.cheney.utils.mybatis.plugin.routing.format.RoutingFormatter;

import java.lang.annotation.*;

/**
 * 分表路由信息
 *
 * @author cheney
 * @date 2019-11-10
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Routing {

    String table();

    String dependColumn();

    RoutingType type() default RoutingType.DATE;

    Class<? extends RoutingFormatter> formatter() default MonthRoutingFormatter.class;

    enum RoutingType {
        DATE
    }

}
