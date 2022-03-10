package com._5icodes.starter.redisson.lock;

import com._5icodes.starter.common.advisor.AbstractCachedAdvisor;
import com._5icodes.starter.common.infrastructure.CachingMetadataReaderFactoryProvider;
import com._5icodes.starter.redisson.RedissonConstants;
import org.aopalliance.aop.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;

import java.lang.reflect.Method;

public class LockAdvisor extends AbstractCachedAdvisor<Locked> {
    public LockAdvisor(CachingMetadataReaderFactoryProvider metadataReaderFactoryProvider) {
        super(metadataReaderFactoryProvider);
    }

    @Override
    protected void ifMatches(Locked annotation, String beanName, Method method, Class<?> targetClass) {
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 1000;
    }

    @Override
    @Autowired
    @Qualifier(RedissonConstants.LOCK_INTERCEPTOR_BEAN_NAME)
    public void setAdvice(Advice advice) {
        super.setAdvice(advice);
    }
}