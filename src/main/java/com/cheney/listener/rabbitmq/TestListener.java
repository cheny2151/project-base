package com.cheney.listener.rabbitmq;

import com.rabbitmq.client.Channel;
import org.apache.log4j.Logger;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.ChannelAwareMessageListener;
import org.springframework.amqp.support.converter.SimpleMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;

public class TestListener implements ChannelAwareMessageListener {

    private final Logger logger = Logger.getLogger(this.getClass());

    @Autowired
    private SimpleMessageConverter simpleMessageConverter;

    @Override
    public void onMessage(Message message, Channel channel) throws IOException {
        Object object = simpleMessageConverter.fromMessage(message);
//        logger.info(object.getClass());
        logger.info(Thread.currentThread().getId() + ":" + object);
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

}
