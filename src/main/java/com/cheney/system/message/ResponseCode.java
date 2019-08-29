package com.cheney.system.message;

public enum ResponseCode {

    SUCCESS("Success", 200),

    ERROR("Server Error", 500),

    USER_NOT_LOGIN("用户未登录", -1),

    USERNAME_NOT_FOUND("Username not found", -100);

    private String msg;

    private int status;

    ResponseCode(String msg, int status) {
        this.msg = msg;
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public int getStatus() {
        return status;
    }

}
