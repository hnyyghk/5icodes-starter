package com._5icodes.starter.rocketmq.producer;

import com._5icodes.starter.rocketmq.interceptor.MessageInterceptorList;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.rocketmq.common.message.Message;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.aspectj.MethodInvocationProceedingJoinPoint;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Collections;

@Slf4j
@Aspect
public class RocketmqProducerAspect {
    private static final Field methodInvocationField;

    static {
        try {
            methodInvocationField = MethodInvocationProceedingJoinPoint.class.getDeclaredField("methodInvocation");
            methodInvocationField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private final MessageInterceptorList interceptorList;

    public RocketmqProducerAspect(MessageInterceptorList interceptorList) {
        this.interceptorList = interceptorList;
    }

    @Pointcut("execution(* org.apache.rocketmq.client.producer.MQProducer.send*(..))")
    public void sendPointcut() {
    }

    @Around("sendPointcut()")
    public Object doSendBefore(ProceedingJoinPoint pjp) throws Throwable {
        if (!(pjp instanceof MethodInvocationProceedingJoinPoint)) {
            return pjp.proceed();
        }
        MethodInvocationProceedingJoinPoint joinPoint = (MethodInvocationProceedingJoinPoint) pjp;
        Object[] args = joinPoint.getArgs();
        if (ArrayUtils.isEmpty(args)) {
            return pjp.proceed();
        }
        Object arg = args[0];
        Collection<Message> messages = extractMessages(joinPoint, arg);
        if (CollectionUtils.isEmpty(messages)) {
            return pjp.proceed();
        }
        return interceptorList.send(messages, pjp);
    }

    private Collection<Message> extractMessages(MethodInvocationProceedingJoinPoint joinPoint, Object arg) throws IllegalAccessException {
        ProxyMethodInvocation proxyMethodInvocation = (ProxyMethodInvocation) methodInvocationField.get(joinPoint);
        Method method = proxyMethodInvocation.getMethod();
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        Type genericParameterType = genericParameterTypes[0];
        Collection<Message> messages = null;
        if (Message.class.equals(genericParameterType)) {
            messages = Collections.singletonList((Message) arg);
        } else if (genericParameterType instanceof ParameterizedType) {
            ParameterizedType parameterType = (ParameterizedType) genericParameterType;
            Type rawType = parameterType.getRawType();
            if (Collection.class.equals(rawType)) {
                Type[] actualTypeArguments = parameterType.getActualTypeArguments();
                if (ArrayUtils.isNotEmpty(actualTypeArguments) && Message.class.equals(actualTypeArguments[0])) {
                    messages = (Collection<Message>) arg;
                }
            }
        }
        return messages;
    }
}