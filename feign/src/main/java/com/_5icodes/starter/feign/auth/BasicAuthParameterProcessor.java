package com._5icodes.starter.feign.auth;

import com._5icodes.starter.feign.custom.AnnotatedMethodProcessor;
import feign.MethodMetadata;
import feign.RequestInterceptor;
import feign.auth.BasicAuthRequestInterceptor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

public class BasicAuthParameterProcessor implements AnnotatedMethodProcessor {
    private final RequestInterceptor authInterceptor;

    public BasicAuthParameterProcessor(String userName, String password) {
        this.authInterceptor = new BasicAuthRequestInterceptor(userName, password, StandardCharsets.UTF_8);
    }

    public RequestInterceptor getAuthInterceptor() {
        return authInterceptor;
    }

    @Override
    public void process(MethodMetadata data, Annotation methodAnnotation, Method method) {
        authInterceptor.apply(data.template());
    }

    @Override
    public Class<? extends Annotation> getAnnotationType() {
        return BasicAuth.class;
    }
}