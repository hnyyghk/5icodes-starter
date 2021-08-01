package com._5icodes.starter.webmvc.feign;

import com._5icodes.starter.feign.AnnotationConfigHolder;

import java.lang.annotation.Annotation;

public class CodeMsgDecodeAnnotationConfigHolder implements AnnotationConfigHolder {
    @Override
    public Class<? extends Annotation> annotationType() {
        return CodeMsgDecode.class;
    }

    @Override
    public Class<?> configClass() {
        return CodeMsgDecoderConfiguration.class;
    }
}