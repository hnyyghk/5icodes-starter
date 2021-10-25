package com._5icodes.starter.gray.request;

import com._5icodes.starter.gray.config.ConfigurableFactory;
import com._5icodes.starter.gray.parser.DelegateJsonParser;

import java.util.List;

public class RequestPredicateJsonParser extends DelegateJsonParser<RequestPredicate> {
    public RequestPredicateJsonParser(List<ConfigurableFactory<?, RequestPredicate>> delegates) {
        super(delegates);
    }
}