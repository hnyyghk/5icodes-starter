package com._5icodes.starter.feign.utils;

import lombok.experimental.UtilityClass;
import org.springframework.cloud.openfeign.FeignClientSpecification;

@UtilityClass
public class FeignReflectionUtils {
    private final Class<?> specificationClass;

    static {
        try {
            specificationClass = Class.forName(FeignClientSpecification.class.getName());
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> getSpecificationClass() {
        return specificationClass;
    }
}