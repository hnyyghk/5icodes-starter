package com._5icodes.starter.async.registry;

public class NotRetryRuleAttribute extends RetryRuleAttribute {
    public NotRetryRuleAttribute(Class<?> clazz) {
        super(clazz);
    }

    public NotRetryRuleAttribute(String exceptionName) {
        super(exceptionName);
    }
}