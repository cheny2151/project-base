package com.cheney.utils.entityCopy.annotation;

import com.cheney.utils.entityCopy.DefaultFieldCopyAdopt;
import com.cheney.utils.entityCopy.FieldCopyAdopt;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CopyAsField {

    String as() default "";

    Class<? extends FieldCopyAdopt> use() default DefaultFieldCopyAdopt.class;

}
