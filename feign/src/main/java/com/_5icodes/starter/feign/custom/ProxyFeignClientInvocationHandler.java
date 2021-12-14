package com._5icodes.starter.feign.custom;

import com._5icodes.starter.common.utils.ExceptionUtils;
import com._5icodes.starter.feign.FeignConstants;
import com._5icodes.starter.feign.utils.TimeoutKeyUtils;
import com.netflix.config.CachedDynamicLongProperty;
import feign.Request;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class ProxyFeignClientInvocationHandler implements InvocationHandler {
    private final Object bean;

    private final Class<?> type;

    private final String contextId;

    private volatile boolean initialed = false;

    private final Map<Method, Pair<DynamicLongFuncProperty, DynamicLongFuncProperty>> methodTimeoutMap = new ConcurrentHashMap<>();

    private static class GlobalDynamicTimeout {
        //feign.client.config.default.connectTimeout: 1000
        //feign.client.config.default.readTimeout: 3000
        static CachedDynamicLongProperty connectTimeout = new CachedDynamicLongProperty("feign.client.config.default.connectTimeout", FeignConstants.DEFAULT_CONNECT_TIMEOUT);
        static CachedDynamicLongProperty readTimeout = new CachedDynamicLongProperty("feign.client.config.default.readTimeout", FeignConstants.DEFAULT_READ_TIMEOUT);
    }

    public ProxyFeignClientInvocationHandler(Object bean, Class<?> type, String contextId) {
        this.bean = bean;
        this.type = type;
        this.contextId = contextId;
    }

    private synchronized void initial() {
        if (initialed) {
            return;
        }
        //feign.client.config.contextId.connectTimeout: 1000
        //feign.client.config.contextId.readTimeout: 3000
        DynamicLongFuncProperty classConnectTimeout = new DynamicLongFuncProperty("feign.client.config." + contextId + ".connectTimeout", GlobalDynamicTimeout.connectTimeout::get);
        DynamicLongFuncProperty classReadTimeout = new DynamicLongFuncProperty("feign.client.config." + contextId + ".readTimeout", GlobalDynamicTimeout.readTimeout::get);
        ReflectionUtils.doWithMethods(type, method -> {
            //feign.client.config.contextId.method.connectTimeout: 1000
            //feign.client.config.contextId.method.readTimeout: 3000
            DynamicLongFuncProperty methodConnectTimeout = new DynamicLongFuncProperty(TimeoutKeyUtils.connectTimeoutKey(type, method, contextId), classConnectTimeout::getValue);
            DynamicLongFuncProperty methodReadTimeout = new DynamicLongFuncProperty(TimeoutKeyUtils.readTimeoutKey(type, method, contextId), classReadTimeout::getValue);
            methodTimeoutMap.put(method, Pair.of(methodConnectTimeout, methodReadTimeout));
        });
        initialed = true;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!initialed) {
            initial();
        }
        Pair<DynamicLongFuncProperty, DynamicLongFuncProperty> pair = methodTimeoutMap.get(method);
        if (null == pair) {
            return doInvoke(method, args);
        }
        try {
            FeignRequestOptionsContext.set(new Request.Options(pair.getLeft().getValue(), TimeUnit.MILLISECONDS, pair.getRight().getValue(), TimeUnit.MILLISECONDS, true));
            return doInvoke(method, args);
        } finally {
            FeignRequestOptionsContext.remove();
        }
    }

    private Object doInvoke(Method method, Object[] args) throws Throwable {
        try {
            return method.invoke(bean, args);
        } catch (Exception e) {
            throw ExceptionUtils.getRealException(e);
        }
    }
}