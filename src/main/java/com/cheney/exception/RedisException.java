package com.cheney.exception;

/**
 * @author cheney
 */
public class RedisException extends RuntimeException {
    public RedisException() {
    }

    public RedisException(String message) {
        super(message);
    }

    public RedisException(String message, Throwable cause) {
        super(message, cause);
    }

}
