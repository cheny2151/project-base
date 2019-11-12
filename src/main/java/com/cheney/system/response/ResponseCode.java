package com.cheney.system.response;

public enum ResponseCode {

    SUCCESS("success", 200),

    ERROR("Server Error", -500),

    USER_NOT_LOGIN("用户未登录", -1),

    USERNAME_OR_PASSWORD_ERROR("用户名或密码错误", -2),

    ERROR_PASSWORD("密码错误", -3),

    NOT_EXISTS_USER("用户不存在", -4),

    FORBIDDEN("无访问权限", -5),

    USERNAME_NOT_FOUND("Username not found", -100),

    PARAM_EMPTY("必要参数为空", -1000),

    PARAM_ERROR("参数错误", -1001);

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
