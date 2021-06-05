package com._5icodes.starter.feign.custom;

import feign.MethodMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * @see org.springframework.cloud.openfeign.AnnotatedParameterProcessor
 */
public interface AnnotatedMethodProcessor {
    void process(MethodMetadata data, Annotation methodAnnotation, Method method);

    Class<? extends Annotation> getAnnotationType();
}