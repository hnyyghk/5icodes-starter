package com._5icodes.starter.feign.custom;

import com._5icodes.starter.common.utils.ExceptionUtils;
import com._5icodes.starter.feign.FeignConstants;
import com._5icodes.starter.feign.utils.TimeoutKeyUtils;
import com.netflix.config.CachedDynamicLongProperty;
import feign.Request;
import lombok.Getter;
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

    @Getter
    private final String name;

    private volatile boolean initialed = false;

    private final Map<Method, Pair<DynamicLongFuncProperty, DynamicLongFuncProperty>> methodTimeoutMap = new ConcurrentHashMap<>();

    private static class GlobalDynamicTimeout {
        static CachedDynamicLongProperty connectTimeout = new CachedDynamicLongProperty("ribbon.ConnectTimeout", FeignConstants.DEFAULT_CONNECT_TIMEOUT);
        static CachedDynamicLongProperty readTimeout = new CachedDynamicLongProperty("ribbon.ReadTimeout", FeignConstants.DEFAULT_READ_TIMEOUT);
    }

    public ProxyFeignClientInvocationHandler(Object bean, Class<?> type, String name) {
        this.bean = bean;
        this.type = type;
        this.name = name;
    }

    private synchronized void initial() {
        if (initialed) {
            return;
        }
        DynamicLongFuncProperty classConnectTimeout = new DynamicLongFuncProperty(name + ".ribbon.ConnectTimeout", GlobalDynamicTimeout.connectTimeout::get);
        DynamicLongFuncProperty classReadTimeout = new DynamicLongFuncProperty(name + ".ribbon.ReadTimeout", GlobalDynamicTimeout.readTimeout::get);
        ReflectionUtils.doWithMethods(type, method -> {
            DynamicLongFuncProperty methodConnectTimeout = new DynamicLongFuncProperty(TimeoutKeyUtils.connectTimeoutKey(type, method), classConnectTimeout::getValue);
            DynamicLongFuncProperty methodReadTimeout = new DynamicLongFuncProperty(TimeoutKeyUtils.readTimeoutKey(type, method), classReadTimeout::getValue);
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