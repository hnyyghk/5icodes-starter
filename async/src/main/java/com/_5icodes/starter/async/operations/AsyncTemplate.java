package com._5icodes.starter.async.operations;

import com._5icodes.starter.async.AsyncContext;
import com._5icodes.starter.async.callback.AsyncCallback;
import com._5icodes.starter.async.delay.Delayer;
import com._5icodes.starter.async.policy.DelayTimeLevel;
import com._5icodes.starter.async.policy.RetryPolicy;
import com._5icodes.starter.async.registry.AsyncRegistry;
import com._5icodes.starter.async.registry.AsyncRetryProperties;
import com._5icodes.starter.async.registry.NotRetryRuleAttribute;
import com._5icodes.starter.async.registry.RetryRuleAttribute;
import com._5icodes.starter.common.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.function.BiConsumer;

@Slf4j(topic = "com._5icodes.starter.async.operations.AsyncOperations")
public class AsyncTemplate extends AbstractAsyncOperations {
    private final AsyncRegistry registry;

    public AsyncTemplate(AsyncRegistry registry, Delayer delayer) {
        super(delayer);
        this.registry = registry;
    }

    @Override
    protected DelayTimeLevel getDelayTimeByPolicy(AsyncContext context) {
        AsyncRetryProperties properties = registry.get(context.getMethod());
        RetryPolicy retryPolicy = properties.getRetryPolicy();
        return retryPolicy.getNextRetryDelayTime(context);
    }

    @Override
    protected void applySuccessCallback(AsyncContext context) {
        doCallback(context, (c, callback) -> {
            try {
                callback.success(c);
            } catch (Exception e) {
                log.error("success callback failed", e);
            }
        });
    }

    @Override
    protected void applyFailOnceCallback(AsyncContext context) {
        doCallback(context, (c, callback) -> {
            try {
                callback.failOnce(c);
            } catch (Exception e) {
                log.error("failOnce callback failed", e);
            }
        });
    }

    @Override
    protected void applyFailFinalCallback(AsyncContext context) {
        doCallback(context, (c, callback) -> {
            try {
                callback.failFinal(c);
            } catch (Exception e) {
                log.error("failFinal callback failed", e);
            }
        });
    }

    protected void doCallback(AsyncContext context, BiConsumer<AsyncContext, AsyncCallback> callbackBiConsumer) {
        AsyncRetryProperties properties = registry.get(context.getMethod());
        AsyncCallback callback = getCallback(properties);
        if (callback != null) {
            callbackBiConsumer.accept(context, callback);
        }
    }

    protected AsyncCallback getCallback(AsyncRetryProperties properties) {
        AsyncCallback callback = properties.getCallback();
        if (callback != null) {
            return callback;
        }
        Class<? extends AsyncCallback> callbackClass = properties.getCallbackClass();
        if (callbackClass != null) {
            try {
                callback = SpringUtils.getBean(callbackClass);
                properties.setCallback(callback);
                return callback;
            } catch (Exception e) {
                log.warn("try to find AsyncCallback class: {} failed, this callback will be ignored", callbackClass.getName(), e);
                properties.setCallbackClass(null);
            }
        }
        String callbackName = properties.getCallbackName();
        if (StringUtils.hasText(callbackName)) {
            try {
                callback = SpringUtils.getBean(callbackName, AsyncCallback.class);
                properties.setCallback(callback);
                return callback;
            } catch (Exception e) {
                log.warn("try to find AsyncCallback bean: {} failed, this callback will be ignored", callbackName, e);
                properties.setCallbackName(null);
            }
        }
        return null;
    }

    @Override
    protected boolean shouldRetryByExceptionRules(AsyncContext context, Exception e) {
        AsyncRetryProperties properties = registry.get(context.getMethod());
        RetryRuleAttribute winner = null;
        int deepest = Integer.MAX_VALUE;
        List<RetryRuleAttribute> retryRules = properties.getRetryRules();
        if (retryRules != null) {
            for (RetryRuleAttribute retryRule : retryRules) {
                int depth = retryRule.getDepth(e);
                if (depth >= 0 && depth < deepest) {
                    deepest = depth;
                    winner = retryRule;
                }
            }
        }
        if (winner == null) {
            return e instanceof RuntimeException;
        } else {
            return !(winner instanceof NotRetryRuleAttribute);
        }
    }

    @Override
    public void register(Method method, AsyncRetryProperties properties) {
        registry.register(method, properties);
    }
}