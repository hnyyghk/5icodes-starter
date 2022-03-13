package com._5icodes.starter.async.annotation;

import com._5icodes.starter.async.AsyncContext;
import com._5icodes.starter.async.callback.CallbackContext;
import com._5icodes.starter.async.operations.AsyncOperations;
import com._5icodes.starter.common.advisor.AbstractCachedInterceptor;
import com._5icodes.starter.common.advisor.MethodExpressionEvaluator;
import com._5icodes.starter.common.utils.TraceUtils;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

public class AsyncInterceptor extends AbstractCachedInterceptor<AsyncRun> implements ApplicationContextAware {
    private ApplicationContext applicationContext;
    private final MethodExpressionEvaluator evaluator = new MethodExpressionEvaluator();
    private final AsyncOperations asyncOperations;

    public AsyncInterceptor(AsyncOperations asyncOperations) {
        this.asyncOperations = asyncOperations;
    }

    @Override
    protected Object doWithAnnotation(MethodInvocation invocation, AsyncRun annotation, String beanName) throws Throwable {
        if (CallbackContext.get() != null) {
            return invocation.proceed();
        }
        AsyncContext context = generateAsyncContext(invocation, annotation, beanName);
        Mode mode = annotation.mode();
        switch (mode) {
            case ASYNC:
                asyncOperations.runAsync(context);
                break;
            case PROTECTION:
                return asyncOperations.runAndCheckAsync(context);
            case FAIL_RETRY:
                return asyncOperations.runAndRetryAsyncIfFailed(context);
            default:
                break;
        }
        return null;
    }

    private AsyncContext generateAsyncContext(MethodInvocation invocation, AsyncRun annotation, String beanName) {
        AsyncContext context = new AsyncContext();
        context.setBeanName(beanName);
        context.setMethod(invocation.getMethod());
        context.setArguments(invocation.getArguments());
        context.setTarget(invocation.getThis());
        context.setOrderly(annotation.orderly());
        context.setRetryTimes(0);
        String id = annotation.id();
        if (!StringUtils.hasText(id)) {
            id = TraceUtils.getReqId();
        } else {
            id = String.valueOf(evaluator.key(invocation, id, applicationContext));
        }
        context.setId(id);
        return context;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}