package com._5icodes.starter.async.registry;

import org.springframework.util.Assert;

public class RetryRuleAttribute {
    private final String exceptionName;

    public RetryRuleAttribute(Class<?> clazz) {
        Assert.notNull(clazz, "clazz cannot be null");
        if (!Throwable.class.isAssignableFrom(clazz)) {
            throw new IllegalArgumentException(
                    "Cannot construct rollback rule from [" + clazz.getName() + "]: it's not a Throwable");
        }
        this.exceptionName = clazz.getName();
    }

    public RetryRuleAttribute(String exceptionName) {
        Assert.hasText(exceptionName, "exceptionName cannot be null or empty");
        this.exceptionName = exceptionName;
    }

    public String getExceptionName() {
        return exceptionName;
    }

    public int getDepth(Throwable e) {
        return getDepth(e.getClass(), 0);
    }

    private int getDepth(Class<?> exceptionClass, int depth) {
        if (exceptionClass.getName().contains(exceptionName)) {
            // Found it!
            return depth;
        }
        // If we've gone as far as we can go and haven't found it...
        if (exceptionClass == Throwable.class) {
            return -1;
        }
        return getDepth(exceptionClass.getSuperclass(), depth + 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof RetryRuleAttribute)) {
            return false;
        }
        RetryRuleAttribute retryRule = (RetryRuleAttribute) obj;
        return exceptionName.equals(retryRule.exceptionName);
    }
}