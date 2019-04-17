package com.cheney.listener;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 监听spring context的启动和刷新事件
 *
 * @author cheney
 * @date 2019/4/17
 */
@Component
public class ApplicationInitListener implements ApplicationListener<ContextRefreshedEvent> {

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {

    }

}
