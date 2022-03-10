package com._5icodes.starter.common.advisor;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.AnnotatedElementKey;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.CachedExpressionEvaluator;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.expression.Expression;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MethodExpressionEvaluator extends CachedExpressionEvaluator {
    private static final Map<ExpressionKey, Expression> KEY_CACHE = new ConcurrentHashMap<>(16);
    private static final Map<AnnotatedElementKey, Method> TARGET_METHOD_CACHE = new ConcurrentHashMap<>(16);

    public Object key(MethodInvocation invocation, String keyExpression, BeanFactory beanFactory) {
        Method method = invocation.getMethod();
        Object[] arguments = invocation.getArguments();
        Object target = invocation.getThis();
        Class<?> targetClass = target != null ? AopUtils.getTargetClass(target) : null;
        MethodExpressionRootObject rootObject = new MethodExpressionRootObject(method, arguments, target, targetClass);
        Method targetMethod = getTargetMethod(targetClass, method);
        MethodBasedEvaluationContext evaluationContext = new MethodBasedEvaluationContext(rootObject, targetMethod, arguments, getParameterNameDiscoverer());
        if (beanFactory != null) {
            evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));
        }
        AnnotatedElementKey methodKey = new AnnotatedElementKey(method, targetClass);
        return getExpression(KEY_CACHE, methodKey, keyExpression).getValue(evaluationContext);
    }

    private Method getTargetMethod(Class<?> targetClass, Method method) {
        AnnotatedElementKey methodKey = new AnnotatedElementKey(method, targetClass);
        return TARGET_METHOD_CACHE.computeIfAbsent(methodKey, annotatedElementKey -> {
            Method targetMethod = AopUtils.getMostSpecificMethod(method, targetClass);
            return targetMethod == null ? method : targetMethod;
        });
    }
}