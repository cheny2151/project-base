package com.cheney.aop;

import com.cheney.entity.dto.BaseEntity;
import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Date;

//@Component
//@Aspect
public class MybatisAop {

    private final Logger logger = Logger.getLogger(MybatisAop.class);

    @Pointcut("target(com.cheney.dao.*)")
    public void mergePoint() {
    }

    @Around("mergePoint()")
    public Object mergeHandler(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("-------------------aop---------------------");
        Object[] args = joinPoint.getArgs();
        BaseEntity baseEntity = (BaseEntity) args[0];
        baseEntity.setCreateDate(new Date());
        return joinPoint.proceed();
    }

}
