package com.cheney.exception;

/**
 * 校验异常
 *
 * @Date 2021/1/30
 * @Created by chenyi
 */
public class ValidateException extends RuntimeException {

    public ValidateException(String message) {
        super(message);
    }
}
