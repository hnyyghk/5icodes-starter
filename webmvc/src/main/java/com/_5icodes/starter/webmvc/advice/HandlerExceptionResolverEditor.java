package com._5icodes.starter.webmvc.advice;

import com._5icodes.starter.webmvc.result.DelegateHandlerMethodReturnValueHandler;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.handler.HandlerExceptionResolverComposite;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.util.ArrayList;
import java.util.List;

public class HandlerExceptionResolverEditor implements SmartInitializingSingleton, ApplicationContextAware {
    private ApplicationContext applicationContext;

    @Override
    public void afterSingletonsInstantiated() {
        HandlerExceptionResolver handlerExceptionResolver = applicationContext.getBean("handlerExceptionResolver", HandlerExceptionResolver.class);
        if (!(handlerExceptionResolver instanceof HandlerExceptionResolverComposite)) {
            return;
        }
        HandlerExceptionResolverComposite composite = (HandlerExceptionResolverComposite) handlerExceptionResolver;
        List<HandlerExceptionResolver> exceptionResolvers = composite.getExceptionResolvers();
        for (HandlerExceptionResolver exceptionResolver : exceptionResolvers) {
            if (!(exceptionResolver instanceof ExceptionHandlerExceptionResolver)) {
                continue;
            }
            ExceptionHandlerExceptionResolver resolver = (ExceptionHandlerExceptionResolver) exceptionResolver;
            HandlerMethodReturnValueHandlerComposite returnValueHandlers = resolver.getReturnValueHandlers();
            List<HandlerMethodReturnValueHandler> handlers = returnValueHandlers.getHandlers();
            List<HandlerMethodReturnValueHandler> wrappedHandlers = new ArrayList<>();
            for (HandlerMethodReturnValueHandler handler : handlers) {
                wrappedHandlers.add(new DelegateHandlerMethodReturnValueHandler(handler));
            }
            resolver.setReturnValueHandlers(wrappedHandlers);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}