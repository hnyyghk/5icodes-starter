package com._5icodes.starter.gray.request.predicate;

import com._5icodes.starter.gray.request.RequestPredicate;
import com._5icodes.starter.gray.request.RequestPredicateFactory;
import com._5icodes.starter.sleuth.utils.BaggageFieldUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class WhiteListRequestPredicateFactory implements RequestPredicateFactory<WhiteListRequestPredicateFactory.Config> {
    @Override
    public RequestPredicate apply(Config config) {
        Set<String> whiteList = config.getWhiteList();
        String header = config.getHeader();
        return () -> {
            String headerVal = BaggageFieldUtils.get(header);
            boolean predicate = whiteList.contains(headerVal);
            log.trace("request header {}: {} predicate result: {}", header, headerVal, predicate);
            return predicate;
        };
    }

    @Data
    public static class Config {
        private String header;
        private Set<String> whiteList;
    }
}