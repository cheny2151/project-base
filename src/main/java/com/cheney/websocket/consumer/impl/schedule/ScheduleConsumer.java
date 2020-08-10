package com.cheney.websocket.consumer.impl.schedule;

import com.cheney.websocket.dto.Message;

import javax.websocket.Session;

/**
 * 进度信息消费者
 *
 * @author cheney
 * @date 2020-08-10
 */
public interface ScheduleConsumer {

    /**
     * 消费进度信息
     *
     * @param message websocket信息
     * @param session websocket session
     */
    void consume(Message message, Session session);

    /**
     * 进度消息类型
     */
    String type();
}
