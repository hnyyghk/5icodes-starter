package com._5icodes.starter.common.condition;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

import java.util.function.Function;

public abstract class AbstractEnabledSpringBootCondition<T> extends SpringBootCondition {
    private final String prefix;

    private final Class<T> actualType;

    private final Function<T, Boolean> enableFunc;

    private static final SimpleMapEnabledConditionOutcomeCache OUTCOME_CACHE = SimpleMapEnabledConditionOutcomeCache.getInstance();

    public AbstractEnabledSpringBootCondition(String prefix, Class<T> actualType, Function<T, Boolean> enableFunc) {
        this.prefix = prefix;
        this.actualType = actualType;
        this.enableFunc = enableFunc;
    }

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return OUTCOME_CACHE.computeIfAbsent(this.getClass(), aClass -> {
            Binder binder = Binder.get(context.getEnvironment());
            BindResult<T> bind = binder.bind(prefix, actualType);
            boolean bound = bind.isBound();
            if (!bound) {
                return ConditionOutcome.match();
            }
            T properties = bind.get();
            return enableFunc.apply(properties) ? ConditionOutcome.match() : ConditionOutcome.noMatch(String.format("%s not match", aClass.getSimpleName()));
        });
    }
}