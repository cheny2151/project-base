package com.cheney.javaconfig.mq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * javaConfig的mq配置（声明队列和交换器）
 *
 * @author cheney
 */
@Configuration
public class RabbitMqBeanConfig {

    /**
     * 声明队列
     */
    @Bean
    public Queue testForExchange() {
        String queueName = "test_for_exchange";
        boolean durable = true;
        boolean exclusive = false;
        boolean autoDelete = false;
        return new Queue(queueName, durable, exclusive, autoDelete, null);
    }

    /**
     * 声明交换机
     */
    @Bean
    public Exchange exTopic() {
        String exchangeName = "ex_topic";
        boolean durable = true;
        boolean autoDelete = false;
        return new TopicExchange(exchangeName, durable, autoDelete);
    }

    /**
     * 绑定交换机与队列
     */
    @Bean
    public Binding bindingTestForExchange() {
        return BindingBuilder.bind(testForExchange())
                .to(exTopic()).with("*").noargs();
    }


}
