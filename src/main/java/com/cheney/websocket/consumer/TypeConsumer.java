package com.cheney.websocket.consumer;



import com.cheney.websocket.dto.Message;

import javax.websocket.Session;

/**
 * websocket--消息类型消费者接口
 *
 * @author cheney
 * @date 2019/6/21
 */
public interface TypeConsumer {

    void consume(Message msg, Session session);

}
