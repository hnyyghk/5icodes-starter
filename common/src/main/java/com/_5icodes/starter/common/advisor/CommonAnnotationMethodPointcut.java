package com._5icodes.starter.common.advisor;

import com._5icodes.starter.common.infrastructure.CachingMetadataReaderFactoryProvider;
import org.springframework.aop.ClassFilter;
import org.springframework.aop.framework.autoproxy.ProxyCreationContext;
import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class CommonAnnotationMethodPointcut<T extends Annotation> extends StaticMethodMatcherPointcut {
    private final Class<T> annotationType;
    private final BeanAnnotationHolder<T> beanAnnotationHolder;
    private final CachingMetadataReaderFactoryProvider metadataReaderFactoryProvider;

    public CommonAnnotationMethodPointcut(Class<T> annotationType, BeanAnnotationHolder<T> beanAnnotationHolder, CachingMetadataReaderFactoryProvider metadataReaderFactoryProvider) {
        this.annotationType = annotationType;
        this.beanAnnotationHolder = beanAnnotationHolder;
        this.metadataReaderFactoryProvider = metadataReaderFactoryProvider;
    }

    @Override
    public ClassFilter getClassFilter() {
        return clazz -> {
            String beanName = getBeanName();
            if (beanName == null) {
                return false;
            }
            Class<?> userClass = ClassUtils.getUserClass(clazz);
            MetadataReader metadataReader = metadataReaderFactoryProvider.getMetadataReader(userClass.getName());
            if (metadataReader == null) {
                return false;
            }
            AnnotationMetadata annotationMetadata = metadataReader.getAnnotationMetadata();
            return annotationMetadata.hasAnnotatedMethods(annotationType.getName());
        };
    }

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        Class<?> userClass = ClassUtils.getUserClass(targetClass);
        Method m = BridgeMethodResolver.findBridgedMethod(method);
        String beanName = getBeanName();
        //真正执行方法
        if (beanName == null) {
            return beanAnnotationHolder.getBeanAnnotation(userClass, m) != null;
        }
        //启动阶段，由于classFilter已经过滤了没有注解方法的类，一定返回true
        Method[] declaredMethods = userClass.getDeclaredMethods();
        for (Method declaredMethod : declaredMethods) {
            T annotation = declaredMethod.getAnnotation(annotationType);
            if (annotation == null) {
                continue;
            }
            ifMatches(annotation, beanName, declaredMethod, userClass);
            beanAnnotationHolder.hold(userClass, declaredMethod, new BeanAnnotation<>(beanName, annotation));
        }
        return true;
    }

    private String getBeanName() {
        return ProxyCreationContext.getCurrentProxiedBeanName();
    }

    protected void ifMatches(T annotation, String beanName, Method method, Class<?> targetClass) {
    }
}