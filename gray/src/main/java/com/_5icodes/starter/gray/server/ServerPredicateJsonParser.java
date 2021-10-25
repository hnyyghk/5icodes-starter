package com._5icodes.starter.gray.server;

import com._5icodes.starter.gray.config.ConfigurableFactory;
import com._5icodes.starter.gray.parser.DelegateJsonParser;

import java.util.List;

public class ServerPredicateJsonParser extends DelegateJsonParser<ServerPredicate> {
    public ServerPredicateJsonParser(List<ConfigurableFactory<?, ServerPredicate>> delegates) {
        super(delegates);
    }
}