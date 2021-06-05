package com._5icodes.starter.feign.client;

import com._5icodes.starter.feign.utils.FeignReflectionUtils;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * feign url mapping for local env
 */
public class LocalFeignClientPostProcessor implements BeanFactoryPostProcessor, EnvironmentAware, Ordered {
    private Map<String, String> mappings;
    private static final Field NAME_FIELD;
    private static final Field URL_FIELD;

    static {
        try {
            Class<?> feignFactoryClass = FeignReflectionUtils.getFeignFactoryClass();
            NAME_FIELD = feignFactoryClass.getDeclaredField("name");
            URL_FIELD = feignFactoryClass.getDeclaredField("url");
            NAME_FIELD.setAccessible(true);
            URL_FIELD.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException("reflect FeignClientFactoryBean failed", e);
        }
    }

    @Override
    @SneakyThrows
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (CollectionUtils.isEmpty(mappings)) {
            return;
        }
        Class<?> feignFactoryClass = FeignReflectionUtils.getFeignFactoryClass();
        Map<String, ?> feignClientFactoryBeanMap = beanFactory.getBeansOfType(feignFactoryClass);
        if (CollectionUtils.isEmpty(feignClientFactoryBeanMap)) {
            return;
        }
        for (Map.Entry<String, ?> entry : feignClientFactoryBeanMap.entrySet()) {
            Object value = entry.getValue();
            String name = (String) NAME_FIELD.get(value);
            String url = (String) URL_FIELD.get(value);
            String urlMapping = mappings.get(name);
            if (!StringUtils.hasText(url) && StringUtils.hasText(urlMapping)) {
                URL_FIELD.set(value, urlMapping);
            }
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        Binder binder = Binder.get(environment);
        BindResult<Map<String, String>> result = binder.bind("local.feign.mapping", Bindable.mapOf(String.class, String.class));
        if (result.isBound()) {
            mappings = result.get();
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}