package com._5icodes.starter.feign.utils;

import com._5icodes.starter.common.utils.PropertySourceUtils;
import com._5icodes.starter.feign.custom.FeignClientCustom;
import lombok.AllArgsConstructor;
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
        add(FeignClientCustom::ReadTimeout, s -> StringUtils.isEmpty(s) ? "ribbon.ReadTimeout" : s + ".ribbon.ReadTimeout", FeignClientCustom.class);
        add(FeignClientCustom::ConnectTimeout, s -> StringUtils.isEmpty(s) ? "ribbon.ConnectTimeout" : s + ".ribbon.ConnectTimeout", FeignClientCustom.class);
        add(FeignClientCustom::MaxAutoRetries, s -> StringUtils.isEmpty(s) ? "ribbon.MaxAutoRetries" : s + ".ribbon.MaxAutoRetries", FeignClientCustom.class);
        add(FeignClientCustom::MaxAutoRetriesNextServer, s -> StringUtils.isEmpty(s) ? "ribbon.MaxAutoRetriesNextServer" : s + ".ribbon.MaxAutoRetriesNextServer", FeignClientCustom.class);
        add(FeignClientCustom::OkToRetryOnAllOperations, s -> StringUtils.isEmpty(s) ? "ribbon.OkToRetryOnAllOperations" : s + ".ribbon.OkToRetryOnAllOperations", FeignClientCustom.class);

        add(FeignClientCustom::loggerLevel, s -> StringUtils.isEmpty(s) ? "feign.client.config.default.loggerLevel" : "feign.client.config." + s + ".loggerLevel", FeignClientCustom.class);
        add(FeignClientCustom::errorDecoder, s -> StringUtils.isEmpty(s) ? "feign.client.config.default.errorDecoder" : "feign.client.config." + s + ".errorDecoder", FeignClientCustom.class);
        add(FeignClientCustom::requestInterceptors, s -> StringUtils.isEmpty(s) ? "feign.client.config.default.requestInterceptors" : "feign.client.config." + s + ".requestInterceptors", FeignClientCustom.class);
        add(FeignClientCustom::decode404, s -> StringUtils.isEmpty(s) ? "feign.client.config.default.decode404" : "feign.client.config." + s + ".decode404", FeignClientCustom.class);
        add(FeignClientCustom::encoder, s -> StringUtils.isEmpty(s) ? "feign.client.config.default.encoder" : "feign.client.config." + s + ".encoder", FeignClientCustom.class);
        add(FeignClientCustom::decoder, s -> StringUtils.isEmpty(s) ? "feign.client.config.default.decoder" : "feign.client.config." + s + ".decoder", FeignClientCustom.class);
        add(FeignClientCustom::contract, s -> StringUtils.isEmpty(s) ? "feign.client.config.default.contract" : "feign.client.config." + s + ".contract", FeignClientCustom.class);
    }

    public static <T> void process(T t, ConfigurableEnvironment environment, String commandKey, Class<T> tClass) {
        List<Holder<?>> holders = REGISTRY.get(tClass);
        for (Holder holder : holders) {
            Function<String, String> keyFunc = holder.keyFunc;
            Function<T, String> valFunc = holder.valFunc;
            String key = keyFunc.apply(commandKey);
            String val = valFunc.apply(t);
            if (StringUtils.hasLength(val)) {
                PropertySourceUtils.put(environment, key, val);
            }
        }
    }

    private static <T> void add(Function<T, String> valFunc, Function<String, String> keyFunc, Class<T> tClass) {
        List<Holder<?>> holders = REGISTRY.computeIfAbsent(tClass, k -> new LinkedList<>());
        holders.add(new Holder<>(valFunc, keyFunc));
    }

    @AllArgsConstructor
    private static class Holder<T> {
        private Function<T, String> valFunc;
        private Function<String, String> keyFunc;
    }
}