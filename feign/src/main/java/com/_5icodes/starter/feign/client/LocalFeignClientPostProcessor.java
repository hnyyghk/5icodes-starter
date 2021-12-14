package com._5icodes.starter.feign.client;

import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientFactoryBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * feign url mapping for local env
 *
 * @see org.springframework.cloud.openfeign.FeignClientsRegistrar#registerFeignClient
 */
public class LocalFeignClientPostProcessor implements BeanFactoryPostProcessor, EnvironmentAware, Ordered {
    private Map<String, String> mappings;

    @Override
    @SneakyThrows
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (CollectionUtils.isEmpty(mappings)) {
            return;
        }
        String[] beanNames = beanFactory.getBeanNamesForAnnotation(FeignClient.class);
        for (String beanName : beanNames) {
            AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) beanFactory.getBeanDefinition(beanName);
            FeignClientFactoryBean feignClientFactoryBean = (FeignClientFactoryBean) beanDefinition.getAttribute("feignClientsRegistrarFactoryBean");
            String contextId = feignClientFactoryBean.getContextId();
            String localMapping = mappings.get(contextId);
            if (StringUtils.isEmpty(localMapping)) {
                continue;
            }
            Supplier<?> instanceSupplier = beanDefinition.getInstanceSupplier();
            Field field = instanceSupplier.getClass().getDeclaredField("arg$4");
            field.setAccessible(true);
            AnnotationAttributes annotationAttributes = (AnnotationAttributes) field.get(instanceSupplier);
            String url = (String) annotationAttributes.get("url");
            if (StringUtils.hasText(url)) {
                continue;
            }
            annotationAttributes.put("url", localMapping);
        }
    }

    @Override
    public void setEnvironment(Environment environment) {
        Binder binder = Binder.get(environment);
        BindResult<Map<String, Object>> bindResult = binder.bind("feign.client.config", Bindable.mapOf(String.class, Object.class));
        if (bindResult.isBound()) {
            Map<String, String> mappings = new HashMap<>();
            Map<String, Object> properties = bindResult.get();
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                if (entry.getValue() instanceof Map) {
                    String localMapping = (String) ((Map<?, ?>) entry.getValue()).get("localMapping");
                    if (StringUtils.hasText(localMapping)) {
                        mappings.put(entry.getKey(), localMapping);
                    }
                }
            }
            if (!mappings.isEmpty()) {
                this.mappings = mappings;
            }
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}