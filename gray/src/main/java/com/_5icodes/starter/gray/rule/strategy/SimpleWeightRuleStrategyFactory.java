package com._5icodes.starter.gray.rule.strategy;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.gray.SleuthStrategyContext;
import com._5icodes.starter.gray.exception.ParseException;
import com._5icodes.starter.gray.parser.JsonParser;
import com._5icodes.starter.gray.request.RequestPredicate;
import com._5icodes.starter.gray.rule.RuleStrategy;
import com._5icodes.starter.gray.rule.RuleStrategyFactory;
import com._5icodes.starter.gray.server.ServerPredicate;
import com._5icodes.starter.gray.utils.ServerUtils;
import com._5icodes.starter.gray.weight.ServerWeightLoadBalance;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class SimpleWeightRuleStrategyFactory implements RuleStrategyFactory<Map<String, String>> {
    private final JsonParser<ServerWeightLoadBalance> weightLoadBalanceJsonParser;
    private final JsonParser<RequestPredicate> requestPredicateJsonParser;
    private final JsonParser<ServerPredicate> serverPredicateJsonParser;

    private static final String CUSTOM = "custom";

    public SimpleWeightRuleStrategyFactory(JsonParser<ServerWeightLoadBalance> weightLoadBalanceJsonParser,
                                           JsonParser<RequestPredicate> requestPredicateJsonParser,
                                           JsonParser<ServerPredicate> serverPredicateJsonParser) {
        this.weightLoadBalanceJsonParser = weightLoadBalanceJsonParser;
        this.requestPredicateJsonParser = requestPredicateJsonParser;
        this.serverPredicateJsonParser = serverPredicateJsonParser;
    }

    @Override
    public RuleStrategy apply(Map<String, String> config) throws ParseException {
        ServerWeightLoadBalance weightLoadBalance = weightLoadBalanceJsonParser.parse(config);
        //有配置自定义规则的
        if (config.containsKey(CUSTOM)) {
            String configStr = config.get(CUSTOM);
            SimpleMappingRuleStrategyFactory.Config custom = JsonUtils.parse(configStr, SimpleMappingRuleStrategyFactory.Config.class);
            Map<String, String> configRequest = custom.getRequest();
            Map<String, String> configServer = custom.getServer();
            RequestPredicate requestPredicate = requestPredicateJsonParser.parse(configRequest);
            ServerPredicate serverPredicate = serverPredicateJsonParser.parse(configServer);
            return new RuleStrategy() {
                @Override
                public ServiceInstance choose(List<ServiceInstance> candidates) {
                    List<ServiceInstance> filtered = new ArrayList<>(candidates.size());
                    boolean testRequest = requestPredicate.test();
                    for (ServiceInstance candidate : candidates) {
                        boolean testServer = serverPredicate.test(candidate);
                        if (testRequest == testServer) {
                            filtered.add(candidate);
                        }
                    }
                    SleuthStrategyContext.set(testRequest && filtered.size() > 0);
                    if (filtered.size() == 0) {
                        log.trace("custom filtered servers are empty, fall back to weightLoadBalance");
                        ServiceInstance server = weightLoadBalance.choose(candidates);
                        return server == null ? RuleStrategy.super.choose(candidates) : server;
                    }
                    log.trace("custom filtered servers are {}", ServerUtils.getServerStr(filtered));
                    return RuleStrategy.super.choose(filtered);
                }
            };
        }
        return new RuleStrategy() {
            @Override
            public ServiceInstance choose(List<ServiceInstance> candidates) {
                ServiceInstance server = weightLoadBalance.choose(candidates);
                return server == null ? RuleStrategy.super.choose(candidates) : server;
            }
        };
    }
}