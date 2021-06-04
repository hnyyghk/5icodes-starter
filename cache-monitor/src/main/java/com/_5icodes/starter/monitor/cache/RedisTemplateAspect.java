package com._5icodes.starter.monitor.cache;

import com._5icodes.starter.monitor.cache.exception.ForbiddenOperationException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.data.redis.core.ValueOperations;

@Aspect
public class RedisTemplateAspect {
    @Pointcut("execution(* org.springframework.data.redis.core.RedisTemplate.keys(..))")
    public void keysPointcut() {
    }

    @Pointcut("execution(* org.springframework.data.redis.core.RedisTemplate.opsForValue(..))")
    public void opsForValuePointcut() {
    }

    @Before("keysPointcut()")
    public void before() {
        throw new ForbiddenOperationException("keys operation is forbidden");
    }

    @Around("opsForValuePointcut()")
    public Object opsForValueAround(ProceedingJoinPoint point) throws Throwable {
        Object result = point.proceed();
        return new CustomValueOperations((ValueOperations) result);
    }
}