package com._5icodes.starter.gray.request;

import com._5icodes.starter.gray.config.ConfigurableFactory;

public interface RequestPredicateFactory<C> extends ConfigurableFactory<C, RequestPredicate> {
    @Override
    default String shortName() {
        String simpleName = this.getClass().getSimpleName();
        int index = simpleName.indexOf(RequestPredicateFactory.class.getSimpleName());
        return simpleName.substring(0, index);
    }

    @Override
    default Class<RequestPredicate> factoryClass() {
        return RequestPredicate.class;
    }
}