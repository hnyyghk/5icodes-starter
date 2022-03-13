package com._5icodes.starter.async.registry;

import com._5icodes.starter.async.callback.AsyncCallback;
import com._5icodes.starter.async.policy.RetryPolicy;
import lombok.Data;

import java.util.List;

@Data
public class AsyncRetryProperties {
    private List<RetryRuleAttribute> retryRules;
    private RetryPolicy retryPolicy;
    private AsyncCallback callback;
    private String callbackName;
    private Class<? extends AsyncCallback> callbackClass;
}