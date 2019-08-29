package com.cheney.javaconfig;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * 任务调度配置
 * @author cheney
 */
@Configuration
@EnableScheduling
@Slf4j
public class ScheduledConfig implements SchedulingConfigurer {

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        //配置线程池
        scheduledTaskRegistrar.setTaskScheduler(threadPoolTaskScheduler());
    }

    /**
     * spring任务调度配置bean
     */
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
        executor.setThreadNamePrefix("taskScheduler-");
        // 当调度器shutdown被调用时等待当前被调度的任务完成
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 等待时常
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }

    /**
     * 任务线程池bean
     */
    @Bean("taskExecutor")
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(50);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("taskExecutor-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(60);
        return executor;
    }


}
