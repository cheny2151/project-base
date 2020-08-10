package com.cheney.websocket.server;

import com.cheney.websocket.dto.Message;
import com.cheney.websocket.utils.MessageTypeHolder;
import com.cheney.websocket.utils.SessionHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;

/**
 * websocket公用业务类
 *
 * @author cheney
 * @date 2019/6/21
 */
@ServerEndpoint("/admin/ws/common")
@Component
@Slf4j
public class CommonWebSocketServer {

    private SessionHolder sessionHolder = SessionHolder.getOnly();

    @OnOpen
    public void onOpen(Session session) {
        log.info("[websocket]新的连接打开,sessionId->{}", session.getId());
        sessionHolder.put(session.getId(), session);
    }

    @OnClose
    public void onClose(Session session) {
        log.info("[websocket]连接关闭,sessionId->{}", session.getId());
        sessionHolder.remove(session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        Message msgEntity;
        try {
            msgEntity = Message.parse(message);
        } catch (Exception e) {
            log.error("[websocket]解析消息异常:msg:{},error:{}", message, e.getMessage());
            return;
        }

        log.info("[websocket]接受消息:requestId->{};type->{};message->{}", msgEntity.getRequestId(), msgEntity.getType(), msgEntity.getData());
        //根据消息执行不同业务
        MessageTypeHolder.consume(msgEntity, session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("[websocket]sessionId->{},异常{}", session.getId(), error.getMessage());
        if (!session.isOpen()) {
            sessionHolder.remove(session.getId());
        }
    }

    @Scheduled(cron = "0 0 0/2 * * ?")
    public void clearInvalidSession() {
        log.info("执行websocket失效session缓存清除任务，清除前缓存个数{}", sessionHolder.size());
        sessionHolder.clear();
        log.info("清除后缓存个数{}", sessionHolder.size());
    }

}
