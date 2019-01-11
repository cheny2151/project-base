package com.cheney.javaconfig;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.Connector;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistry;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 优雅停机，监听tomcat的ContextClosedEvent事件
 */
@Configuration
@Slf4j
public class ShutdownConfig {

    /**
     * 用于接受shutdown事件
     *
     * @return
     */
    @Bean("gracefulShutdown")
    public GracefulShutdown gracefulShutdown(RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry) {
        return new GracefulShutdown(rabbitListenerEndpointRegistry);
    }

    /**
     * 用于注入 connector
     *
     * @return
     */
    @Bean
    public ServletWebServerFactory tomcatCustomizer(GracefulShutdown gracefulShutdown) {
        TomcatServletWebServerFactory tomcatServletWebServerFactory = new TomcatServletWebServerFactory();
        tomcatServletWebServerFactory.addConnectorCustomizers(gracefulShutdown);
        return tomcatServletWebServerFactory;
    }

    private static class GracefulShutdown implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {
        private volatile Connector connector;
        private final int waitTime = 120;
        private volatile RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry;

        public GracefulShutdown(RabbitListenerEndpointRegistry rabbitListenerEndpointRegistry) {
            this.rabbitListenerEndpointRegistry = rabbitListenerEndpointRegistry;
        }

        @Override
        public void customize(Connector connector) {
            this.connector = connector;
        }

        @Override
        public void onApplicationEvent(ContextClosedEvent event) {
            log.info("Graceful shutdown start");
            this.connector.pause();
            this.rabbitListenerEndpointRegistry.stop();

            Executor executor = this.connector.getProtocolHandler().getExecutor();
            if (executor instanceof ThreadPoolExecutor) {
                try {
                    ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
                    threadPoolExecutor.shutdown();
                    if (!threadPoolExecutor.awaitTermination(waitTime, TimeUnit.SECONDS)) {
                        log.warn("Tomcat process cannot terminate in {}s, try enforced termination", waitTime);
                    }
                    log.info("Graceful shutdown end");
                } catch (Exception e) {
                    log.error("Graceful shutdown exception", e);
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
}