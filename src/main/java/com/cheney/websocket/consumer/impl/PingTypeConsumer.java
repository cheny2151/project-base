package com.cheney.websocket.consumer.impl;


import com.cheney.websocket.consumer.TypeConsumer;
import com.cheney.websocket.dto.Message;
import com.cheney.websocket.utils.SessionHolder;

import javax.websocket.Session;

/**
 * pingType消费者
 *
 * @author cheney
 * @date 2019/6/21
 */
public class PingTypeConsumer implements TypeConsumer {

    @Override
    public void consume(Message msg, Session session) {
        SessionHolder.getOnly().sendMsg(session, "pong");
    }

}
