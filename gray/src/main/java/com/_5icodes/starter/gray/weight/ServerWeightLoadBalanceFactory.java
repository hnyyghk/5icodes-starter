package com._5icodes.starter.gray.weight;

import com._5icodes.starter.gray.config.ConfigurableFactory;

public interface ServerWeightLoadBalanceFactory<C> extends ConfigurableFactory<C, ServerWeightLoadBalance> {
    @Override
    default String shortName() {
        String simpleName = this.getClass().getSimpleName();
        int index = simpleName.indexOf(ServerWeightLoadBalanceFactory.class.getSimpleName());
        return simpleName.substring(0, index);
    }

    @Override
    default Class<ServerWeightLoadBalance> factoryClass() {
        return ServerWeightLoadBalance.class;
    }
}