package com._5icodes.starter.feign.utils;

import feign.Feign;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Method;

@UtilityClass
public class TimeoutKeyUtils {
    public String connectTimeoutKey(Class<?> type, Method method) {
        return "feign." + Feign.configKey(type, method) + ".ConnectTimeout";
    }

    public String readTimeoutKey(Class<?> type, Method method) {
        return "feign." + Feign.configKey(type, method) + ".ReadTimeout";
    }
}