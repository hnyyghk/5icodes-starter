package com._5icodes.starter.feign.utils;

import com._5icodes.starter.common.utils.PropertySourceUtils;
import com._5icodes.starter.feign.custom.FeignClientCustom;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class FeignPropertyUtils {
    private static final Map<Class<?>, List<Holder<?>>> REGISTRY = new HashMap<>();

    static {
        add(contextId -> parseFeignContextId(contextId, "loggerLevel"), FeignClientCustom::loggerLevel, FeignClientCustom.class);
        add(contextId -> parseFeignContextId(contextId, "connectTimeout"), FeignClientCustom::connectTimeout, FeignClientCustom.class);
        add(contextId -> parseFeignContextId(contextId, "readTimeout"), FeignClientCustom::readTimeout, FeignClientCustom.class);
        add(contextId -> parseFeignContextId(contextId, "retryer"), FeignClientCustom::retryer, FeignClientCustom.class);
        add(contextId -> parseFeignContextId(contextId, "errorDecoder"), FeignClientCustom::errorDecoder, FeignClientCustom.class);
        add(contextId -> parseFeignContextId(contextId, "requestInterceptors"), FeignClientCustom::requestInterceptors, FeignClientCustom.class);
        add(contextId -> parseFeignContextId(contextId, "defaultRequestHeaders"), FeignClientCustom::defaultRequestHeaders, FeignClientCustom.class);
        add(contextId -> parseFeignContextId(contextId, "defaultQueryParameters"), FeignClientCustom::defaultQueryParameters, FeignClientCustom.class);
        add(contextId -> parseFeignContextId(contextId, "decode404"), FeignClientCustom::decode404, FeignClientCustom.class);
        add(contextId -> parseFeignContextId(contextId, "decoder"), FeignClientCustom::decoder, FeignClientCustom.class);
        add(contextId -> parseFeignContextId(contextId, "encoder"), FeignClientCustom::encoder, FeignClientCustom.class);
        add(contextId -> parseFeignContextId(contextId, "contract"), FeignClientCustom::contract, FeignClientCustom.class);
        add(contextId -> parseFeignContextId(contextId, "exceptionPropagationPolicy"), FeignClientCustom::exceptionPropagationPolicy, FeignClientCustom.class);
        add(contextId -> parseFeignContextId(contextId, "capabilities"), FeignClientCustom::capabilities, FeignClientCustom.class);
        add(contextId -> parseFeignContextId(contextId, "metrics"), FeignClientCustom::metrics, FeignClientCustom.class);
        add(contextId -> parseFeignContextId(contextId, "followRedirects"), FeignClientCustom::followRedirects, FeignClientCustom.class);

        add(contextId -> parseLoadBalancerContextId(contextId, "retry.retryOnAllOperations"), FeignClientCustom::retryOnAllOperations, FeignClientCustom.class);
        add(contextId -> parseLoadBalancerContextId(contextId, "retry.maxRetriesOnSameServiceInstance"), FeignClientCustom::maxRetriesOnSameServiceInstance, FeignClientCustom.class);
        add(contextId -> parseLoadBalancerContextId(contextId, "retry.maxRetriesOnNextServiceInstance"), FeignClientCustom::maxRetriesOnNextServiceInstance, FeignClientCustom.class);
    }

    private static String parseFeignContextId(String contextId, String propertiesKey) {
        //feign.client.config.contextId.propertiesKey
        //feign.client.config.default.propertiesKey
        return "feign.client.config." + (StringUtils.hasText(contextId) ? contextId : "default") + "." + propertiesKey;
    }

    private static String parseLoadBalancerContextId(String contextId, String propertiesKey) {
        //spring.cloud.loadbalancer.contextId.propertiesKey
        //spring.cloud.loadbalancer.propertiesKey
        return "spring.cloud.loadbalancer." + (StringUtils.hasText(contextId) ? contextId + "." : "") + propertiesKey;
    }

    public static <T> void process(T t, ConfigurableEnvironment environment, String commandKey, Class<T> tClass) {
        List<Holder<?>> holders = REGISTRY.get(tClass);
        for (Holder<?> holder : holders) {
            Function<String, String> keyFunc = holder.getKeyFunc();
            Function<T, String> valFunc = (Function<T, String>) holder.getValFunc();
            String key = keyFunc.apply(commandKey);
            String val = valFunc.apply(t);
            if (StringUtils.hasText(val)) {
                PropertySourceUtils.put(environment, key, val);
            }
        }
    }

    private static <T> void add(Function<String, String> keyFunc, Function<T, String> valFunc, Class<T> tClass) {
        List<Holder<?>> holders = REGISTRY.computeIfAbsent(tClass, k -> new LinkedList<>());
        holders.add(new Holder<>(keyFunc, valFunc));
    }

    @AllArgsConstructor
    @Getter
    private static class Holder<T> {
        private Function<String, String> keyFunc;
        private Function<T, String> valFunc;
    }
}