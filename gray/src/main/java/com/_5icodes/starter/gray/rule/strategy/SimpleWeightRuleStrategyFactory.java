package com._5icodes.starter.gray.rule.strategy;

import com._5icodes.starter.gray.exception.ParseException;
import com._5icodes.starter.gray.parser.JsonParser;
import com._5icodes.starter.gray.rule.RuleStrategy;
import com._5icodes.starter.gray.rule.RuleStrategyFactory;
import com._5icodes.starter.gray.weight.ServerWeightLoadBalance;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;
import java.util.Map;

public class SimpleWeightRuleStrategyFactory implements RuleStrategyFactory<Map<String, String>> {
    private final JsonParser<ServerWeightLoadBalance> weightLoadBalanceJsonParser;

    public SimpleWeightRuleStrategyFactory(JsonParser<ServerWeightLoadBalance> weightLoadBalanceJsonParser) {
        this.weightLoadBalanceJsonParser = weightLoadBalanceJsonParser;
    }

    @Override
    public RuleStrategy apply(Map<String, String> config) throws ParseException {
        ServerWeightLoadBalance weightLoadBalance = weightLoadBalanceJsonParser.parse(config);
        return new RuleStrategy() {
            @Override
            public ServiceInstance choose(List<ServiceInstance> candidates) {
                ServiceInstance server = weightLoadBalance.choose(candidates);
                return server == null ? RuleStrategy.super.choose(candidates) : server;
            }
        };
    }
}