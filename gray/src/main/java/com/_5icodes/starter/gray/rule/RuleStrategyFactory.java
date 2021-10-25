package com._5icodes.starter.gray.rule;

import com._5icodes.starter.gray.config.ConfigurableFactory;

public interface RuleStrategyFactory<C> extends ConfigurableFactory<C, RuleStrategy> {
    @Override
    default String shortName() {
        String simpleName = this.getClass().getSimpleName();
        int index = simpleName.indexOf(RuleStrategyFactory.class.getSimpleName());
        return simpleName.substring(0, index);
    }

    @Override
    default Class<RuleStrategy> factoryClass() {
        return RuleStrategy.class;
    }
}