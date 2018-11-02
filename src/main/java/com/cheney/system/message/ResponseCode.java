package com.cheney.system.message;

public enum ResponseCode {

    SUCCESS("Success", 200),

    ERROR("Server Error", 500);

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
