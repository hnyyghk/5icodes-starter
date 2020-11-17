package com._5icodes.starter.common.condition;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class SimpleMapEnabledConditionOutcomeCache {
    private static final SimpleMapEnabledConditionOutcomeCache instance = new SimpleMapEnabledConditionOutcomeCache();

    public static SimpleMapEnabledConditionOutcomeCache getInstance() {
        return instance;
    }

    private final Map<Class, ConditionOutcome> cache = new ConcurrentHashMap<>();

    public ConditionOutcome computeIfAbsent(Class clazz, Function<Class, ConditionOutcome> mappingFunction) {
        return cache.computeIfAbsent(clazz, mappingFunction);
    }

    public void clear() {
        cache.clear();
    }
}