package com._5icodes.starter.cache;

import com._5icodes.starter.common.utils.JsonUtils;

import java.util.function.Function;

public class JacksonKeyConvertor implements Function<Object, Object> {
    public static final JacksonKeyConvertor INSTANCE = new JacksonKeyConvertor();

    @Override
    public Object apply(Object originalKey) {
        if (originalKey == null) {
            return null;
        }
        if (originalKey instanceof String) {
            return originalKey;
        }
        return JsonUtils.toJson(originalKey);
    }
}