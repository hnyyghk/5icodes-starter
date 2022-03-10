package com._5icodes.starter.common.utils;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@UtilityClass
public class ClassUtils {
    private final Map<Method, String> configKeyCache = new ConcurrentHashMap<>();

    /**
     * @see feign.Feign#configKey(Class, Method)
     */
    public String configKey(Class<?> targetType, Method m) {
        return configKeyCache.computeIfAbsent(m, method -> {
            StringBuilder builder = new StringBuilder();
            builder.append(targetType.getSimpleName());
            builder.append('#').append(method.getName()).append('(');
            for (Type param : method.getGenericParameterTypes()) {
                param = TypeUtils.resolve(targetType, targetType, param);
                builder.append(TypeUtils.getRawType(param).getSimpleName()).append(',');
            }
            if (method.getParameterTypes().length > 0) {
                builder.deleteCharAt(builder.length() - 1);
            }
            return builder.append(')').toString();
        });
    }
}