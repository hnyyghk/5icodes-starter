package com._5icodes.starter.feign.utils;

import feign.Feign;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Method;

@UtilityClass
public class TimeoutKeyUtils {
    public String connectTimeoutKey(Class<?> type, Method method, String contextId) {
        return "feign.client.config." + Feign.configKey(type, method).replace(type.getSimpleName() + "#", contextId + ".") + ".connectTimeout";
    }

    public String readTimeoutKey(Class<?> type, Method method, String contextId) {
        return "feign.client.config." + Feign.configKey(type, method).replace(type.getSimpleName() + "#", contextId + ".") + ".readTimeout";
    }
}