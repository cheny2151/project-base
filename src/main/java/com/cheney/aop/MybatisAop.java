package com.cheney.aop;

import com.cheney.entity.dto.BaseEntity;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Pointcut;

import java.util.Date;

//@Component
//@Aspect
@Slf4j
public class MybatisAop {

    @Pointcut("target(com.cheney.dao.*)")
    public void mergePoint() {
    }

    @Around("mergePoint()")
    public Object mergeHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("-------------------aop---------------------");
        Object[] args = joinPoint.getArgs();
        BaseEntity baseEntity = (BaseEntity) args[0];
        baseEntity.setCreateDate(new Date());
        return joinPoint.proceed();
    }

}
