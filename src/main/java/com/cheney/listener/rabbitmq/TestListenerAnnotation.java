package com.cheney.listener.rabbitmq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * 注解方式消费消息队列
 *
 * @RabbitListener,@RabbitHandler
 */
@Slf4j
@Component
public class TestListenerAnnotation {

    @RabbitListener(queues = "test_for_exchange")
    public void onMessage(Message message, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag, Channel channel) throws IOException {
        try {
            String body = new String(message.getBody(), Charset.forName("utf-8"));
            log.info(Thread.currentThread().getId() + ":" + body);
            //手动确认需要在application.yml中配置
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("error", e);
            channel.basicReject(deliveryTag, false);
        }
    }

}
