package com.cheney.utils.entityCopy.annotation;

import java.lang.annotation.*;

/**
 * copy实体类型
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CopyAsClass {

    Class<?> as();

}
