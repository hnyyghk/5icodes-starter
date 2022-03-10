package com._5icodes.starter.common.advisor;

import lombok.Setter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public abstract class AbstractCachedInterceptor<T extends Annotation> implements MethodInterceptor {
    @Setter
    private BeanAnnotationHolder<T> beanAnnotationHolder;

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        BeanAnnotation<T> beanAnnotation = getBeanAnnotation(invocation);
        if (beanAnnotation == null) {
            return invocation.proceed();
        }
        T annotation = beanAnnotation.getAnnotation();
        if (annotation == null) {
            return invocation.proceed();
        }
        return doWithAnnotation(invocation, annotation, beanAnnotation.getBeanName());
    }

    protected abstract Object doWithAnnotation(MethodInvocation invocation, T annotation, String beanName) throws Throwable;

    private BeanAnnotation<T> getBeanAnnotation(MethodInvocation invocation) {
        Class<?> targetClass = AopUtils.getTargetClass(invocation.getThis());
        Method method = invocation.getMethod();
        return beanAnnotationHolder.getBeanAnnotation(targetClass, method);
    }
}