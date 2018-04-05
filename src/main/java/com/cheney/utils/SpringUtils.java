package com.cheney.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@Lazy(false)
public class SpringUtils implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    private static Environment env;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringUtils.applicationContext = applicationContext;
        env = applicationContext.getEnvironment();
    }

    public static <T> T getBean(String name, Class<T> tClass) {
        return applicationContext.getBean(name, tClass);
    }

    public static Object getBean(String name) {
        return applicationContext.getBean(name);
    }

    public static Environment getEnvironment() {
        return env;
    }

}
