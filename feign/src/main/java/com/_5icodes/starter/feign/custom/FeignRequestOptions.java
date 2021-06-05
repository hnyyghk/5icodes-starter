package com._5icodes.starter.feign.custom;

import com._5icodes.starter.feign.FeignConstants;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface FeignRequestOptions {
    int connectTimeout() default FeignConstants.NOT_SET;

    int readTimeout() default FeignConstants.NOT_SET;
}