package com._5icodes.starter.gray.rule;

public class DefaultRuleStrategy implements RuleStrategy {
    public static final DefaultRuleStrategy INSTANCE = new DefaultRuleStrategy();

    @Override
    public String toString() {
        return "defaultRuleStrategy";
    }
}