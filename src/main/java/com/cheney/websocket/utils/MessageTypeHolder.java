package com.cheney.websocket.utils;

import com.cheney.websocket.consumer.TypeConsumer;
import com.cheney.websocket.consumer.impl.PingTypeConsumer;
import com.cheney.websocket.consumer.impl.SchedulePushTypeConsumer;
import com.cheney.websocket.consumer.impl.UserNotifyTypeConsumer;
import com.cheney.websocket.dto.Message;
import org.apache.commons.lang.ArrayUtils;

import javax.websocket.Session;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * websocket消息类型
 *
 * @author cheney
 * @date 2019/6/21
 */
public class MessageTypeHolder implements RemoveSessionListener {

    public enum MessageType {

        // PING
        PING(new PingTypeConsumer()),

        // 进度推送
        SCHEDULE_PUSH(new SchedulePushTypeConsumer()),

        // 用户消息通知
        USER_NOTIFY(new UserNotifyTypeConsumer());

        private TypeConsumer typeConsumer;

        MessageType(TypeConsumer typeConsumer) {
            this.typeConsumer = typeConsumer;
        }

        public TypeConsumer getTypeConsumer() {
            return typeConsumer;
        }

        /**
         * 忽略的类型
         */
        private final static String[] IGNORE_TYPE = new String[]{"PING"};

        /**
         * 存放业务消息类型与sessionId的映射关系
         */
        private static ConcurrentHashMap<String, ConcurrentSkipListSet<String>> typeIdHolder = new ConcurrentHashMap<>();

        static {
            for (MessageType type : MessageType.values()) {
                if (!ArrayUtils.contains(IGNORE_TYPE, type.name())) {
                    typeIdHolder.put(type.name(), new ConcurrentSkipListSet<>());
                }
            }
        }

        public void addSessionId(String sessionId) {
            if (!ArrayUtils.contains(IGNORE_TYPE, this.name())) {
                typeIdHolder.get(this.name()).add(sessionId);
            }
        }

        public void removeSessionId(String sessionId) {
            if (!ArrayUtils.contains(IGNORE_TYPE, this.name())) {
                typeIdHolder.get(this.name()).remove(sessionId);
            }
        }

        public Set<String> getSessionIds() {
            return typeIdHolder.get(this.name());
        }

    }

    /**
     * 添加sessionId
     *
     * @param message   消息类型
     * @param sessionId 会话id
     */
    public static void addSessionId(String message, String sessionId) {
        MessageType.valueOf(message).addSessionId(sessionId);
    }

    /**
     * 移除sessionId
     *
     * @param message   消息类型
     * @param sessionId 会话id
     */
    public static void removeSessionId(String message, String sessionId) {
        MessageType.valueOf(message).removeSessionId(sessionId);
    }

    /**
     * 消费websocket消息
     *
     * @param message 消息实体
     * @param session 会话
     */
    public static void consume(Message message, Session session) {
        TypeConsumer typeConsumer = message.getMessageType().getTypeConsumer();
        if (typeConsumer != null) {
            typeConsumer.consume(message, session);
        }
    }

    @Override
    public void beforeRemove(String sessionId) {

    }

    @Override
    public void afterRemove(String sessionId) {
        MessageType.typeIdHolder.values().forEach(e -> e.remove(sessionId));
    }

}
