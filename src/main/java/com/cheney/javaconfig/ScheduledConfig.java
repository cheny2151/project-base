package com.cheney.javaconfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * 任务调度配置
 * @author cheney
 */
@Configuration
@Slf4j
public class ScheduledConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        //配置线程池
        scheduledTaskRegistrar.setTaskScheduler(threadPoolTaskScheduler());
    }

    @Bean
    public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler(){
            private static final long serialVersionUID = -8082780501237070564L;
            public void destroy() {
                log.info("ExecutorService 'threadPoolTaskScheduler' running task count: {}, delayed task count: {}",
                        this.getActiveCount(), this.getScheduledThreadPoolExecutor().getQueue().size());
                this.getScheduledThreadPoolExecutor().setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
                super.destroy();
                log.info("ExecutorService 'threadPoolTaskScheduler' shut down now.");
            }
        };
        executor.setPoolSize(20);
        executor.setThreadNamePrefix("executorForAnnotation-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }


}
