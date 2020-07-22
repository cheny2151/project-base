package com.cheney.javaconfig;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 动态注册spring bean
 * 示例：注册自己
 *
 * @author cheney
 * @date 2020-07-22
 */
public class MyBeanRegistrar implements ImportBeanDefinitionRegistrar {

    public final static String BEAN_NAME = "MyBeanRegistrar";

    private String property;

    private RedisTemplate redisTemplate;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(MyBeanRegistrar.class);
        beanDefinitionBuilder.addPropertyValue("property", "success");
        beanDefinitionBuilder.addPropertyReference("redisTemplate", "redisTemplate");
        registry.registerBeanDefinition(BEAN_NAME, beanDefinitionBuilder.getBeanDefinition());
    }

    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }

    public RedisTemplate getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(RedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
}
