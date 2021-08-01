package com._5icodes.starter.webmvc.common;

import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public interface OnlyOnceInterceptorConfigurer extends WebMvcConfigurer, OnlyOnceHandlerInterceptor {
    @Override
    default void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(this);
        if (this instanceof Ordered) {
            registration.order(((Ordered) this).getOrder());
        }
    }
}