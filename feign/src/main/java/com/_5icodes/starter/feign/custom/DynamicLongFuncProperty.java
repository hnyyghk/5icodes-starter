package com._5icodes.starter.feign.custom;

import com.netflix.config.PropertyWrapper;

import java.util.function.LongSupplier;

public class DynamicLongFuncProperty extends PropertyWrapper<Long> {
    private final LongSupplier supplier;

    public DynamicLongFuncProperty(String propName, LongSupplier supplier) {
        super(propName, supplier.getAsLong());
        this.supplier = supplier;
    }

    @Override
    public Long getValue() {
        Long value = prop.getLong();
        if (null == value) {
            return supplier.getAsLong();
        } else {
            return value;
        }
    }
}