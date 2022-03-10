package com._5icodes.starter.common.advisor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BeanAnnotationHolder<T extends Annotation> {
    private final Map<Class<?>, Map<Method, BeanAnnotation<T>>> classMethodBeanAnnotationMap = new HashMap<>();

    public BeanAnnotation<T> getBeanAnnotation(Class<?> targetClass, Method method) {
        Map<Method, BeanAnnotation<T>> methodBeanAnnotationMap = classMethodBeanAnnotationMap.get(targetClass);
        if (methodBeanAnnotationMap == null) {
            return null;
        }
        return methodBeanAnnotationMap.get(method);
    }

    public void hold(Class<?> targetClass, Method method, BeanAnnotation<T> beanAnnotation) {
        Map<Method, BeanAnnotation<T>> methodBeanAnnotationMap = classMethodBeanAnnotationMap.computeIfAbsent(targetClass, k -> new HashMap<>());
        methodBeanAnnotationMap.put(method, beanAnnotation);
    }
}