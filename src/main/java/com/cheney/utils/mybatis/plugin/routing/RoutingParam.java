package com.cheney.utils.mybatis.plugin.routing;

import java.lang.annotation.*;

/**
 * @author cheney
 * @date 2020-11-29
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RoutingParam {

    String field() default "";

    int paramIndex() default 0;

    ParamType paramType();

    enum ParamType {
        SINGLE,
        OBJECT,
        MAP;
    }

}
