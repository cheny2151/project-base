package com.cheney.javaconfig.registrar;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * 动态注册spring bean
 * 示例：注册自己
 *
 * @author cheney
 * @date 2020-07-22
 */
@Slf4j
public class MyBeanRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(Bean2Register.class);
        beanDefinitionBuilder.addPropertyValue("property", "success");
        log.info("start register bean");
        // 根据类型自动装配
        beanDefinitionBuilder.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        registry.registerBeanDefinition(Bean2Register.BEAN_NAME, beanDefinitionBuilder.getBeanDefinition());
    }

}
