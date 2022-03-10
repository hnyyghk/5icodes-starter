package com._5icodes.starter.common.advisor;

import com._5icodes.starter.common.infrastructure.CachingMetadataReaderFactoryProvider;
import org.aopalliance.aop.Advice;
import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class AbstractCachedAdvisor<T extends Annotation> extends AbstractBeanFactoryPointcutAdvisor {
    private final CachingMetadataReaderFactoryProvider metadataReaderFactoryProvider;

    public AbstractCachedAdvisor(CachingMetadataReaderFactoryProvider metadataReaderFactoryProvider) {
        this.metadataReaderFactoryProvider = metadataReaderFactoryProvider;
    }

    private final BeanAnnotationHolder<T> beanAnnotationHolder = new BeanAnnotationHolder<>();

    private final Class<T> annotationType;

    {
        ParameterizedType parameterizedType = (ParameterizedType) this.getClass().getGenericSuperclass();
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        annotationType = (Class<T>) actualTypeArguments[0];
    }

    @Override
    public Pointcut getPointcut() {
        return new CommonAnnotationMethodPointcut<T>(annotationType, beanAnnotationHolder, metadataReaderFactoryProvider) {
            @Override
            protected void ifMatches(T annotation, String beanName, Method method, Class<?> targetClass) {
                AbstractCachedAdvisor.this.ifMatches(annotation, beanName, method, targetClass);
            }
        };
    }

    protected abstract void ifMatches(T annotation, String beanName, Method method, Class<?> targetClass);

    @Override
    public void setAdvice(Advice advice) {
        if (advice instanceof AbstractCachedInterceptor) {
            AbstractCachedInterceptor<T> cachedInterceptor = (AbstractCachedInterceptor) advice;
            cachedInterceptor.setBeanAnnotationHolder(beanAnnotationHolder);
        } else {
            throw new IllegalStateException("advice of AbstractCachedAdvisor must be type of AbstractCachedInterceptor");
        }
        super.setAdvice(advice);
    }
}