package com.cheney.javaconfig.mq;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.LoggingProducerListener;
import org.springframework.kafka.support.ProducerListener;

import java.util.Map;

/**
 * @author cheney
 * @date 2019-07-02
 */
//@Configuration
@EnableConfigurationProperties({KafkaProperties.class})
public class KafkaConfig {

    private final KafkaProperties properties;

    public KafkaConfig(KafkaProperties properties) {
        this.properties = properties;
    }

    /**
     * 单独配置一个自定义消费者工厂
     * 此处设置enable.auto.commit=false(enable.auto.commit为kafka原生配置)
     */
    @Bean
    public DefaultKafkaConsumerFactory<String, String> manualConsumerFactory() {
        Map<String, Object> consumerProperties = properties.buildConsumerProperties();
        consumerProperties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        return new DefaultKafkaConsumerFactory<>(consumerProperties);
    }

    /**
     * 单独配置一个自定义消费者工厂
     * 此处设置AckMode.MANUAL
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> manualKafkaListenerContainerFactory(@Qualifier("manualConsumerFactory") ConsumerFactory<String, String> manualConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(manualConsumerFactory);
        ContainerProperties containerProperties = factory.getContainerProperties();
        containerProperties.setAckMode(ContainerProperties.AckMode.MANUAL);
        factory.setBatchListener(true);
        return factory;
    }

    /**
     * 单独配置一个自定的生产者
     */
    @Bean("myKafkaTemplate")
    public KafkaTemplate<String, String> kafkaTemplate(@Qualifier("loggingKafkaProducerListener") ProducerListener<String, String> producerListener,
                                                       @Qualifier("myKafkaProducerFactory") ProducerFactory<String, String> producerFactory) {
        KafkaTemplate<String, String> kafkaTemplate = new KafkaTemplate<>(producerFactory);
        kafkaTemplate.setProducerListener(producerListener);
        return kafkaTemplate;
    }

    @Bean
    public ProducerListener<String, String> loggingKafkaProducerListener() {
        return new LoggingProducerListener<>();
    }

    @Bean
    public ProducerFactory<String, String> myKafkaProducerFactory() {
        return new DefaultKafkaProducerFactory<>(this.properties.buildProducerProperties());
    }

    @Bean
    public NewTopic invoiceTopic() {
        return new NewTopic("test", 10, (short) 2);
    }

}
