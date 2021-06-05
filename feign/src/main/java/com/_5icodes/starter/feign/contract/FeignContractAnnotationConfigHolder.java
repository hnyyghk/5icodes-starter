package com._5icodes.starter.feign.contract;

import com._5icodes.starter.feign.AnnotationConfigHolder;

import java.lang.annotation.Annotation;

public class FeignContractAnnotationConfigHolder implements AnnotationConfigHolder {
    @Override
    public Class<? extends Annotation> annotationType() {
        return EnableFeignContract.class;
    }

    @Override
    public Class<?> configClass() {
        return FeignContractConfiguration.class;
    }
}