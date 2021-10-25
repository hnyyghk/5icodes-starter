package com._5icodes.starter.gray.rule;

import com._5icodes.starter.gray.config.ConfigurableFactory;
import com._5icodes.starter.gray.parser.DelegateJsonParser;

import java.util.List;

public class RuleStrategyJsonParser extends DelegateJsonParser<RuleStrategy> {
    public RuleStrategyJsonParser(List<ConfigurableFactory<?, RuleStrategy>> delegates) {
        super(delegates);
    }
}