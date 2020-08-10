package com.cheney.websocket.consumer.impl.schedule;

import com.cheney.websocket.dto.Message;

/**
 * 进度信息消费者
 *
 * @author cheney
 * @date 2020-08-10
 */
public interface ScheduleConsumer {

    void consume(Message message);

    String type();
}
