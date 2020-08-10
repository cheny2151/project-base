package com.cheney.utils;

import java.io.Serializable;

/**
 * Created by cheny on 2017/9/10.
 */
public class Message implements Serializable {

    private static final long serialVersionUID = -5811883572511091755L;

    public final static int SUCCESS_CODE = 1;

    private final static String SUCCESS_TYPE = "success";

    public final static int ERROR_CODE = 0;

    private final static String ERROR_TYPE = "error";

    private int code;

    /**
     * 消息内容
     */
    private String content;

    private String type;

    public Message(int code, String content) {
        this.code = code;
        if (code == 0) {
            this.type = ERROR_TYPE;
        } else {
            this.type = SUCCESS_TYPE;
        }
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + content + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
