package com.cheney.redis.clustertask.sub;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SubTask {

    String taskId();

}
