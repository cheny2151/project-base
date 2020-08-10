package com.cheney.websocket.consumer.impl;

import com.cheney.websocket.consumer.TypeConsumer;
import com.cheney.websocket.dto.Message;
import com.cheney.websocket.utils.MessageTypeHolder;
import org.springframework.util.CollectionUtils;

import javax.websocket.Session;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 用户消息通知消费者
 *
 * @author cheney
 * @date 2019/6/21
 */
public class UserNotifyTypeConsumer implements TypeConsumer {

    /**
     * 用户id，sessionId映射Map
     */
    private ConcurrentHashMap<Integer, ConcurrentSkipListSet<String>> userSession = new ConcurrentHashMap<>();

    @Override
    public void consume(Message msg, Session session) {
        String sessionId = session.getId();
        MessageTypeHolder.MessageType.USER_NOTIFY.addSessionId(sessionId);
        Integer userId = msg.getData().getInteger("id");
        synchronized (userId.toString()) {
            userSession.computeIfAbsent(userId, (key) -> new ConcurrentSkipListSet<>()).add(sessionId);
        }
    }

    /**
     * 获取与用户绑定的所有sessionIds
     *
     * @param userId 用户id
     * @return sessionIds
     */
    public Set<String> getBindingUserSession(Integer userId) {
        ConcurrentSkipListSet<String> sessionIds = userSession.get(userId);
        return CollectionUtils.isEmpty(sessionIds) ? Collections.emptySet() : sessionIds;
    }

}
