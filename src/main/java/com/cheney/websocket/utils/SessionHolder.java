package com.cheney.websocket.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import javax.websocket.Session;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * websocket会话持有类
 *
 * @author cheney
 * @date 2019/6/21
 */
@Slf4j
public class SessionHolder extends ConcurrentHashMap<String, Session> {

    private final static String LOCK_PRE = "WEBSOCKET_SESSION_%s";

    private List<RemoveSessionListener> removeSessionListeners;

    public SessionHolder(List<RemoveSessionListener> removeSessionListeners) {
        super();
        this.removeSessionListeners = removeSessionListeners;
    }

    public static SessionHolder getOnly() {
        return SessionHolderObject.getSessionHolder();
    }

    public void sendMsgToAll(String msg) {
        Collection<Session> sessions = values();
        log.info("[websocket]发送消息到所有会话,msg->{}", msg);
        if (!CollectionUtils.isEmpty(sessions)) {
            for (Session session : sessions) {
                sendMsg(session, msg);
            }
        }
    }

    public void sendMsgToType(MessageTypeHolder.MessageType type, String msg) {
        Set<String> sessionIds = type.getSessionIds();
        log.info("[websocket]发送消息type->{},msg->{}", type.name(), msg);
        for (String id : sessionIds) {
            Session session = get(id);
            if (session != null && session.isOpen()) {
                sendMsg(session, msg);
            } else {
                // session关闭,从typeMap和sessionMap中移除
                remove(id);
                type.removeSessionId(id);
            }
        }
    }

    public void sendMsg(Set<String> sessionIds, String msg) {
        log.info("[websocket]发送消息,msg->{}", msg);
        Iterator<String> iterator = sessionIds.iterator();
        while (iterator.hasNext()) {
            String id = iterator.next();
            Session session = get(id);
            if (session != null && session.isOpen()) {
                sendMsg(session, msg);
            } else {
                // session关闭,从sessionMap,sessionIds中移除
                remove(id);
                iterator.remove();
            }
        }
    }

    public void sendMsg(Session session, String msg) {
        if (session.isOpen()) {
            synchronized (String.format(LOCK_PRE, session.getId())) {
                try {
                    session.getBasicRemote().sendText(msg);
                } catch (IOException e) {
                    log.error("websocket发送消息失败", e);
                }
            }
        } else {
            // session关闭，从sessionMap中移除
            remove(session.getId());
        }
    }

    @Override
    public Session remove(Object key) {
        removeSessionListeners.forEach(e -> e.beforeRemove((String) key));
        Session remove = super.remove(key);
        removeSessionListeners.forEach(e -> e.afterRemove((String) key));
        return remove;
    }

    @Override
    public void clear() {
        for (Entry<String, Session> entry : this.entrySet()) {
            Session value = entry.getValue();
            if (value == null || !value.isOpen()) {
                this.remove(entry.getKey());
            }
        }
    }

    public void close(Session session) {
        if (session.isOpen()) {
            try {
                session.close();
            } catch (IOException e) {
                log.error("websocket session关闭异常", e);
            }
        }
    }

    /**
     * 单例
     */
    private static class SessionHolderObject {

        private final static SessionHolder SESSION_HOLDER;

        static {
            List<RemoveSessionListener> listeners = new ArrayList<>();
            listeners.add(new MessageTypeHolder());
            SESSION_HOLDER = new SessionHolder(listeners);
        }

        private static SessionHolder getSessionHolder() {
            return SESSION_HOLDER;
        }
    }

}
