package com.cheney.websocket.dto;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cheney.websocket.utils.MessageTypeHolder;
import lombok.Data;

/**
 * @author cheney
 * @date 2019/6/21
 */
@Data
public class Message {

    /**
     * 消费类型
     */
    private String type;

    /**
     * 请求id
     */
    private String requestId;

    /**
     * 消息数据
     */
    private JSONObject data;

    public static Message parse(String msg) {
        return JSON.parseObject(msg, Message.class);
    }

    public MessageTypeHolder.MessageType getMessageType() {
        return MessageTypeHolder.MessageType.valueOf(this.type);
    }
}
