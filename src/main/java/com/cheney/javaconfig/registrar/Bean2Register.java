package com.cheney.javaconfig.registrar;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

/**
 * @author cheney
 * @date 2020-07-26
 */
@Data
@Slf4j
public class Bean2Register implements BeanDefinitionRegistryPostProcessor, InitializingBean {

    public final static String BEAN_NAME = "Bean2Register";

    private String property;

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("running #afterPropertiesSet");
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        log.info("running #postProcessBeanDefinitionRegistry");
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        log.info("running #postProcessBeanFactory");
    }

}
