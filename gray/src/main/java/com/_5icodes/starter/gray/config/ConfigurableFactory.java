package com._5icodes.starter.gray.config;

import com._5icodes.starter.gray.exception.ParseException;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface ConfigurableFactory<C, T> {
    T apply(C config) throws ParseException;

    String shortName();

    default Class<C> configClass() {
        Type[] genericInterfaces = this.getClass().getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) genericInterfaces[0];
        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        return (Class<C>) actualTypeArguments[0];
    }

    Class<T> factoryClass();
}