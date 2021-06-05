package com._5icodes.starter.feign.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class FeignReflectionUtils {
    private final Class<?> specificationClass;
    private final Class<?> feignFactoryClass;

    static {
        try {
            specificationClass = Class.forName("org.springframework.cloud.openfeign.FeignClientSpecification");
            feignFactoryClass = Class.forName("org.springframework.cloud.openfeign.FeignClientFactoryBean");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public Class<?> getSpecificationClass() {
        return specificationClass;
    }

    public Class<?> getFeignFactoryClass() {
        return feignFactoryClass;
    }
}