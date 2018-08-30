package com.cheney.listener.rabbitmq;

import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

@Slf4j
public class TestListener implements ChannelAwareMessageListener {

    @Autowired
    private SimpleMessageConverter simpleMessageConverter;

    @Override
    public void onMessage(Message message, Channel channel) throws IOException {
        Object object = simpleMessageConverter.fromMessage(message);
//        log.info(object.getClass());
        log.info(Thread.currentThread().getId() + ":" + object);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
