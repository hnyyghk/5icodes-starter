package com._5icodes.starter.feign.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FeignReflectionUtils {
    private final Class<?> specificationClass;

    static {
        try {
            specificationClass = Class.forName("org.springframework.cloud.openfeign.FeignClientSpecification");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> getSpecificationClass() {
        return specificationClass;
    }
}