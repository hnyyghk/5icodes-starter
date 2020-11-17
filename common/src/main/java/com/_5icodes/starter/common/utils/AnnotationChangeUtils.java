package com._5icodes.starter.common.utils;

import lombok.experimental.UtilityClass;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@UtilityClass
public class AnnotationChangeUtils {
    public <A extends Annotation> void addAnnotation(Class<?> des, Class<A> aClass, A annotation) {
        processClassAnnotation(des, classAnnotationMap -> classAnnotationMap.put(aClass, annotation));
    }

    public <A extends Annotation> void removeAnnotation(Class<?> des, Class<A> aClass) {
        processClassAnnotation(des, classAnnotationMap -> classAnnotationMap.remove(aClass));
    }

    public <A extends Annotation> void addAnnotation(Method method, Class<A> aClass, A annotation) {
        processMethodAnnotation(method, classAnnotationMap -> classAnnotationMap.put(aClass, annotation));
    }

    public <A extends Annotation> void removeAnnotation(Method method, Class<A> aClass) {
        processMethodAnnotation(method, classAnnotationMap -> classAnnotationMap.remove(aClass));
    }

    private void processClassAnnotation(Class<?> des, Consumer<Map<Class<? extends Annotation>, Annotation>> consumer) {
        des.getDeclaredAnnotations();
        try {
            Class<?>[] declaredClasses = Class.class.getDeclaredClasses();
            Class<?> annotationDataClass = Arrays.stream(declaredClasses)
                    .filter(declaredClass -> "java.lang.Class$AnnotationData".equals(declaredClass.getName())).findAny().get();
            Field annotationDataField = Class.class.getDeclaredField("annotationData");
            ReflectionUtils.makeAccessible(annotationDataField);
            Field annotationsField = annotationDataClass.getDeclaredField("annotations");
            ReflectionUtils.makeAccessible(annotationsField);
            Field declaredAnnotationsField = annotationDataClass.getDeclaredField("declaredAnnotations");
            ReflectionUtils.makeAccessible(declaredAnnotationsField);
            Object annotationData = annotationDataField.get(des);
            Map<Class<? extends Annotation>, Annotation> annotations = (Map<Class<? extends Annotation>, Annotation>) annotationsField.get(annotationData);
            Map<Class<? extends Annotation>, Annotation> declaredAnnotations = (Map<Class<? extends Annotation>, Annotation>) declaredAnnotationsField.get(annotationData);
            if (CollectionUtils.isEmpty(annotations)) {
                annotations = new HashMap<>();
                annotationsField.set(annotationData, annotations);
            }
            if (CollectionUtils.isEmpty(declaredAnnotations)) {
                declaredAnnotations = new HashMap<>();
                declaredAnnotationsField.set(annotationData, declaredAnnotations);
            }
            consumer.accept(annotations);
            consumer.accept(declaredAnnotations);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void processMethodAnnotation(Method method, Consumer<Map<Class<? extends Annotation>, Annotation>> consumer) {
        method.getDeclaredAnnotations();
        try {
            Field declaredAnnotationsField = Executable.class.getDeclaredField("declaredAnnotations");
            ReflectionUtils.makeAccessible(declaredAnnotationsField);
            Map<Class<? extends Annotation>, Annotation> declaredAnnotations = (Map<Class<? extends Annotation>, Annotation>) declaredAnnotationsField.get(method);
            if (CollectionUtils.isEmpty(declaredAnnotations)) {
                declaredAnnotations = new HashMap<>();
                declaredAnnotationsField.set(method, declaredAnnotations);
            }
            consumer.accept(declaredAnnotations);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}