package com._5icodes.starter.gray.weight;

import com._5icodes.starter.gray.config.ConfigurableFactory;
import com._5icodes.starter.gray.parser.DelegateJsonParser;

import java.util.List;

public class ServerWeightLoadBalanceJsonParser extends DelegateJsonParser<ServerWeightLoadBalance> {
    public ServerWeightLoadBalanceJsonParser(List<ConfigurableFactory<?, ServerWeightLoadBalance>> delegates) {
        super(delegates);
    }
}