package com.cheney.websocket.utils;

/**
 * websocket session remove钩子
 *
 * @author cheney
 * @date 2020-03-19
 */
public interface RemoveSessionListener {

    void beforeRemove(String sessionId);

    void afterRemove(String sessionId);

}
