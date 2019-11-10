package com.cheney.utils.annotation;

import java.lang.annotation.*;

/**
 * 指定缓存key
 *
 * @author cheney
 * @date 2019-11-10
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CacheKey {

    String key();

}
