package com._5icodes.starter.feign.custom;

import feign.MethodMetadata;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cloud.openfeign.AnnotatedParameterProcessor;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.core.convert.ConversionService;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomContract extends SpringMvcContract {
    private final Map<Class<? extends Annotation>, AnnotatedMethodProcessor> annotatedMethodProcessorMap = new HashMap<>();

    public CustomContract(List<AnnotatedParameterProcessor> annotatedParameterProcessors,
                          ConversionService conversionService,
                          ObjectProvider<List<AnnotatedMethodProcessor>> annotatedMethodProcessors) {
        super(annotatedParameterProcessors, conversionService);
        annotatedMethodProcessors.ifAvailable(annotatedMethodProcessorList -> {
            for (AnnotatedMethodProcessor annotatedMethodProcessor : annotatedMethodProcessorList) {
                annotatedMethodProcessorMap.put(annotatedMethodProcessor.getAnnotationType(), annotatedMethodProcessor);
            }
        });
    }

    @Override
    protected void processAnnotationOnMethod(MethodMetadata data, Annotation methodAnnotation, Method method) {
        AnnotatedMethodProcessor processor = annotatedMethodProcessorMap.get(methodAnnotation.annotationType());
        if (null != processor) {
            processor.process(data, methodAnnotation, method);
        }
        super.processAnnotationOnMethod(data, methodAnnotation, method);
    }
}