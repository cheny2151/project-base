package com.cheney.listener.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * kafka消息接受监听
 *
 * @author cheney
 * @date 2019-07-11
 */
@Component
@Slf4j
public class KafkaTestListener {

    @KafkaListener(topics = "test", groupId = "test")
    public void onMessage(ConsumerRecord<String, String> data, Acknowledgment ack) {
        log.info("kafka接收消息{}", data.toString());
        ack.acknowledge();
    }

}
