package com.cheney.websocket.consumer.impl.schedule;

import com.cheney.websocket.dto.Message;

import javax.websocket.Session;

/**
 * @author cheney
 * @date 2020-08-10
 */
public class TestScheduleConsumer implements ScheduleConsumer {

    @Override
    public void consume(Message message, Session session) {
        System.out.println(message);
    }

    @Override
    public String type() {
        return "test";
    }

}
