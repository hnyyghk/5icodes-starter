package com._5icodes.starter.async.annotation;

import com._5icodes.starter.async.AsyncConstants;
import com._5icodes.starter.async.AsyncProperties;
import com._5icodes.starter.async.callback.AsyncCallback;
import com._5icodes.starter.async.configuration.OnConcurrentlyCondition;
import com._5icodes.starter.async.configuration.OnOrderlyCondition;
import com._5icodes.starter.async.policy.DefaultRocketRetryPolicy;
import com._5icodes.starter.async.registry.AsyncRetryProperties;
import com._5icodes.starter.async.registry.AsyncRegistry;
import com._5icodes.starter.async.registry.NotRetryRuleAttribute;
import com._5icodes.starter.async.registry.RetryRuleAttribute;
import com._5icodes.starter.common.advisor.AbstractCachedAdvisor;
import com._5icodes.starter.common.infrastructure.CachingMetadataReaderFactoryProvider;
import org.aopalliance.aop.Advice;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AsyncRunAdvisor extends AbstractCachedAdvisor<AsyncRun> {
    private final AsyncProperties asyncProperties;
    private final AsyncRegistry asyncRegistry;

    public AsyncRunAdvisor(CachingMetadataReaderFactoryProvider metadataReaderFactoryProvider, AsyncProperties asyncProperties, AsyncRegistry asyncRegistry) {
        super(metadataReaderFactoryProvider);
        this.asyncProperties = asyncProperties;
        this.asyncRegistry = asyncRegistry;
    }

    @Override
    protected void ifMatches(AsyncRun annotation, String beanName, Method method, Class<?> targetClass) {
        checkGroupTopicPresent(annotation);
        AsyncRetryProperties properties = new AsyncRetryProperties();
        setCallback(annotation, properties);
        setRetryPolicy(annotation, properties);
        setRetryRules(annotation, properties);
        asyncRegistry.register(method, properties);
    }

    protected void setRetryRules(AsyncRun annotation, AsyncRetryProperties properties) {
        Class<? extends Exception>[] retryFor = annotation.retryFor();
        List<RetryRuleAttribute> retryRules = new ArrayList<>();
        for (Class<? extends Exception> retryClass : retryFor) {
            retryRules.add(new RetryRuleAttribute(retryClass));
        }
        for (Class<? extends Exception> notRetryClass : annotation.notRetryFor()) {
            retryRules.add(new NotRetryRuleAttribute(notRetryClass));
        }
        if (retryRules.size() == 0) {
            retryRules.add(new RetryRuleAttribute(RuntimeException.class));
        }
        properties.setRetryRules(retryRules);
    }

    protected void setRetryPolicy(AsyncRun annotation, AsyncRetryProperties properties) {
        DefaultRocketRetryPolicy retryPolicy = new DefaultRocketRetryPolicy();
        retryPolicy.setFirstDelayTime(annotation.firstDelayTime());
        retryPolicy.setStep(annotation.step());
        retryPolicy.setMaxRetry(annotation.maxRetry());
        retryPolicy.setTimeGapOfTimeUnit(annotation.expireTimeUnit(), annotation.expireTime());
        properties.setRetryPolicy(retryPolicy);
    }

    protected void setCallback(AsyncRun annotation, AsyncRetryProperties properties) {
        String callbackName = annotation.callbackName();
        Class<? extends AsyncCallback> callbackClass = annotation.callbackClass();
        if (!callbackClass.equals(AsyncCallback.class)) {
            properties.setCallbackClass(callbackClass);
        } else if (StringUtils.hasText(callbackName)) {
            properties.setCallbackName(callbackName);
        }
    }

    protected void checkGroupTopicPresent(AsyncRun annotation) {
        if (OnConcurrentlyCondition.checkGroupTopicPresent(asyncProperties) && OnOrderlyCondition.checkGroupTopicPresent(asyncProperties)) {
            return;
        }
        if (annotation.orderly()) {
            if (!OnOrderlyCondition.checkGroupTopicPresent(asyncProperties)) {
                throw new BeanCreationException(AsyncConstants.PROPERTY_PREFIX + ".order.group and " + AsyncConstants.PROPERTY_PREFIX + ".order.topics must be configured");
            }
        } else {
            if (!OnConcurrentlyCondition.checkGroupTopicPresent(asyncProperties)) {
                throw new BeanCreationException(AsyncConstants.PROPERTY_PREFIX + ".group and " + AsyncConstants.PROPERTY_PREFIX + ".topics must be configured");
            }
        }
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE + 900;
    }

    @Override
    @Autowired
    @Qualifier(AsyncConstants.ASYNC_INTERCEPTOR_BEAN_NAME)
    public void setAdvice(Advice advice) {
        super.setAdvice(advice);
    }
}