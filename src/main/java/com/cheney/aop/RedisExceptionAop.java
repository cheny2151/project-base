package com.cheney.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 切面处理redis异常
 */
@Component
@Aspect
@Slf4j
public class RedisExceptionAop {

    /**
     * 是否抛出异常
     */
    @Value("${redis.throwRedisException:true}")
    private boolean throwRedisException;

    /**
     * 切点
     */
    @Pointcut("!execution(void||boolean||long com.cheney.redis.*.*(..))&&within(com.cheney.redis.*)")
    public void haveResult() {
    }

    @Pointcut("execution(void com.cheney.redis.*.*(..))")
    public void isVoid() {
    }

    @Pointcut("execution(long||boolean com.cheney.redis.*.*(..))")
    public void returnBase() {
    }

    /**
     * 切点为有返回值的方法
     */
    @Around("haveResult()")
    public Object haveResultHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Throwable e) {
            log.error("redis异常：" + e.getMessage(), e);
            if (throwRedisException) {
                throw e;
            } else {
                return null;
            }
        }
    }

    /**
     * 切点为return void的方法
     */
    @Around("isVoid()")
    public void voidHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            joinPoint.proceed();
        } catch (Throwable e) {
            log.error("redis异常：" + e.getMessage(), e);
            if (throwRedisException) {
                throw e;
            }
        }
    }

    /**
     * 切点为返回基本类型的方法
     */
    @Around("returnBase()")
    public Object baseHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            return joinPoint.proceed();
        } catch (Throwable t) {
            log.error("redis异常：" + t.getMessage());
            throw t;
        }
    }

}
