package com._5icodes.starter.redisson.lock;

import com._5icodes.starter.common.advisor.AbstractCachedInterceptor;
import com._5icodes.starter.common.advisor.MethodExpressionEvaluator;
import com._5icodes.starter.common.utils.ClassUtils;
import lombok.SneakyThrows;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.function.Function;

public class LockInterceptor extends AbstractCachedInterceptor<Locked> implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    private final MethodExpressionEvaluator evaluator = new MethodExpressionEvaluator();

    @Override
    protected Object doWithAnnotation(MethodInvocation invocation, Locked annotation, String beanName) throws Throwable {
        String key = annotation.key();
        if (!StringUtils.hasText(key)) {
            Method method = invocation.getMethod();
            Class<?> targetType = method.getDeclaringClass();
            key = ClassUtils.configKey(targetType, method);
        } else {
            key = String.valueOf(evaluator.key(invocation, key, applicationContext));
        }
        return RedisLockUtils.lockAndApply(key, annotation.waitTime(), annotation.leaseTime(), annotation.timeUnit(), invocation, new Function<MethodInvocation, Object>() {
            @Override
            @SneakyThrows
            public Object apply(MethodInvocation invocation) {
                return invocation.proceed();
            }
        }, annotation.message(), annotation.catchException());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}