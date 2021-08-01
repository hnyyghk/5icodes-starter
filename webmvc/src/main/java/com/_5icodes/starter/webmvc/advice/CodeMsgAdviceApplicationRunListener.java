package com._5icodes.starter.webmvc.advice;

import com._5icodes.starter.common.application.ApplicationRunListenerAdapter;
import com._5icodes.starter.common.utils.AnnotationChangeUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com._5icodes.starter.webmvc.result.CodeMsgResponseBodyAdvice;
import org.springframework.boot.SpringApplication;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.lang.annotation.Annotation;
import java.util.List;

public class CodeMsgAdviceApplicationRunListener extends ApplicationRunListenerAdapter {
    public CodeMsgAdviceApplicationRunListener(SpringApplication application, String[] args) {
        super(application, args);
    }

    @Override
    protected void doStarting() {
        List<String> packages = SpringApplicationUtils.getBasePackages(getApplication());
        final String[] packagesArray = packages.toArray(new String[0]);
        AnnotationChangeUtils.addAnnotation(CodeMsgResponseBodyAdvice.class, RestControllerAdvice.class, new RestControllerAdvice() {
            @Override
            public String[] value() {
                return new String[0];
            }

            @Override
            public String[] basePackages() {
                return packagesArray;
            }

            @Override
            public Class<?>[] basePackageClasses() {
                return new Class[0];
            }

            @Override
            public Class<?>[] assignableTypes() {
                return new Class[0];
            }

            @Override
            public Class<? extends Annotation>[] annotations() {
                return new Class[0];
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return RestControllerAdvice.class;
            }
        });
        super.doStarting();
    }
}