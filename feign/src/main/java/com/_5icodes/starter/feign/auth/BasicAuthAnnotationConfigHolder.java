package com._5icodes.starter.feign.auth;

import com._5icodes.starter.feign.AnnotationConfigHolder;

import java.lang.annotation.Annotation;

public class BasicAuthAnnotationConfigHolder implements AnnotationConfigHolder {
    @Override
    public Class<? extends Annotation> annotationType() {
        return BasicAuth.class;
    }

    @Override
    public Class<?> configClass() {
        return BasicAuthConfiguration.class;
    }
}