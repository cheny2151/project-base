package com.cheney.aop;

import com.cheney.entity.BaseEntity;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 实体更新aop
 *
 * @author cheney
 * @date 2019-11-09
 */
@Component
@Aspect
public class EntityUpdateAop {

    @Pointcut("execution(* com.cheney.service.BaseService.save(*))")
    public void savePoint() {
    }

    @Pointcut("execution(void com.cheney.service.BaseService.update(*))")
    public void updatePoint() {
    }

    @Before("savePoint()")
    public void saveHandler(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        BaseEntity baseEntity = (BaseEntity) args[0];
        baseEntity.setCreateDate(new Date());
    }

    @Before("updatePoint()")
    public void mergeHandler(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        BaseEntity baseEntity = (BaseEntity) args[0];
        baseEntity.setUpdateDate(new Date());
    }

}
