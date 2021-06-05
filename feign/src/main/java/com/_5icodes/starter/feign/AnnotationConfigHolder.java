package com._5icodes.starter.feign;

import java.lang.annotation.Annotation;

public interface AnnotationConfigHolder {
    Class<? extends Annotation> annotationType();

    Class<?> configClass();
}