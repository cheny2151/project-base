package com.cheney.javaconfig.registrar;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * 自定义《实例化感知 Bean 后处理器》
 * 默认的注入实现类：org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor
 *
 * @author by chenyi
 * @date 2022/2/18
 */
@Component
public class MyBeanPostProcessor implements SmartInstantiationAwareBeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return SmartInstantiationAwareBeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
    }

    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        return SmartInstantiationAwareBeanPostProcessor.super.postProcessProperties(pvs, bean, beanName);
    }
}
