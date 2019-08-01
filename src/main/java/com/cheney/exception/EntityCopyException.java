package com.cheney.exception;

/**
 * 实体copy异常类
 *
 * @author cheney
 * @date 2019-08-01
 */
public class EntityCopyException extends RuntimeException {

    public EntityCopyException() {
    }

    public EntityCopyException(String message) {
        super(message);
    }
}
