package com._5icodes.starter.gray.rule.strategy;

import com._5icodes.starter.gray.SleuthStrategyContext;
import com._5icodes.starter.gray.exception.ParseException;
import com._5icodes.starter.gray.parser.JsonParser;
import com._5icodes.starter.gray.request.RequestPredicate;
import com._5icodes.starter.gray.rule.RuleStrategy;
import com._5icodes.starter.gray.rule.RuleStrategyFactory;
import com._5icodes.starter.gray.server.ServerPredicate;
import com._5icodes.starter.gray.utils.ServerUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
public class SimpleMappingRuleStrategyFactory implements RuleStrategyFactory<SimpleMappingRuleStrategyFactory.Config> {
    private final JsonParser<RequestPredicate> requestPredicateJsonParser;
    private final JsonParser<ServerPredicate> serverPredicateJsonParser;

    public SimpleMappingRuleStrategyFactory(JsonParser<RequestPredicate> requestPredicateJsonParser, JsonParser<ServerPredicate> serverPredicateJsonParser) {
        this.requestPredicateJsonParser = requestPredicateJsonParser;
        this.serverPredicateJsonParser = serverPredicateJsonParser;
    }

    @Override
    public RuleStrategy apply(Config config) throws ParseException {
        Map<String, String> configRequest = config.getRequest();
        Map<String, String> configServer = config.getServer();
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
                    log.trace("filtered servers are empty, fall back to all servers.");
                    return RuleStrategy.super.choose(candidates);
                }
                log.trace("filtered servers are {}", ServerUtils.getServerStr(filtered));
                return RuleStrategy.super.choose(filtered);
            }
        };
    }

    @Data
    public static class Config {
        private Map<String, String> request;
        private Map<String, String> server;
    }
}