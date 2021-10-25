package com._5icodes.starter.gray.server;

import com._5icodes.starter.gray.config.ConfigurableFactory;

public interface ServerPredicateFactory<C> extends ConfigurableFactory<C, ServerPredicate> {
    @Override
    default String shortName() {
        String simpleName = this.getClass().getSimpleName();
        int index = simpleName.indexOf(ServerPredicateFactory.class.getSimpleName());
        return simpleName.substring(0, index);
    }

    @Override
    default Class<ServerPredicate> factoryClass() {
        return ServerPredicate.class;
    }
}