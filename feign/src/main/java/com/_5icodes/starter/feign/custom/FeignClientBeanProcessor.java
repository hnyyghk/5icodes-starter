package com._5icodes.starter.feign.custom;

import com._5icodes.starter.common.utils.PropertySourceUtils;
import com._5icodes.starter.feign.AnnotationConfigHolder;
import com._5icodes.starter.feign.utils.FeignPropertyUtils;
import com._5icodes.starter.feign.utils.FeignReflectionUtils;
import com._5icodes.starter.feign.utils.TimeoutKeyUtils;
import com.google.common.collect.Sets;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.FeignClientFactoryBean;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

@Slf4j
public class FeignClientBeanProcessor implements BeanFactoryPostProcessor, EnvironmentAware, BeanPostProcessor {
    private Map<String, Pair<Class<?>, String>> feignBeanMeta = new HashMap<>();

    private final List<AnnotationConfigHolder> annotationConfigHolders;

    public FeignClientBeanProcessor(List<AnnotationConfigHolder> annotationConfigHolders) {
        this.annotationConfigHolders = annotationConfigHolders;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        scanFeignClientBeanDefinition(beanFactory);
        processFeignSpecificationBeanDefinition(beanFactory);
    }

    @Override
    @SneakyThrows
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (null == feignBeanMeta) {
            return bean;
        }
        Pair<Class<?>, String> pair = feignBeanMeta.remove(beanName);
        if (null == pair) {
            return bean;
        }
        if (feignBeanMeta.size() == 0) {
            feignBeanMeta = null;
        }
        Class<?> feignType = pair.getLeft();
        return Proxy.newProxyInstance(ClassUtils.getDefaultClassLoader(), new Class[]{feignType}, new ProxyFeignClientInvocationHandler(bean, feignType, pair.getRight()));
    }

    private void processFeignSpecificationBeanDefinition(ConfigurableListableBeanFactory beanFactory) {
        Map<String, List<Class<?>>> feignClientBeanMeta = new HashMap<>();
        for (Pair<Class<?>, String> c : feignBeanMeta.values()) {
            feignClientBeanMeta.computeIfAbsent(c.getRight(), k -> new ArrayList<>()).add(c.getLeft());
        }
        Map<Class<? extends Annotation>, Class<?>> annotationConfigMap = getAnnotationConfigMap();
        if (CollectionUtils.isEmpty(annotationConfigMap)) {
            return;
        }
        String[] specificationClassBeanNames = beanFactory.getBeanNamesForType(FeignReflectionUtils.getSpecificationClass());
        for (String specificationClassBeanName : specificationClassBeanNames) {
            BeanDefinition specBeanDef = beanFactory.getBeanDefinition(specificationClassBeanName);
            ConstructorArgumentValues argumentValues = specBeanDef.getConstructorArgumentValues();
            ConstructorArgumentValues.ValueHolder nameValueHolder = argumentValues.getArgumentValue(0, String.class);
            String name = (String) nameValueHolder.getValue();
            List<Class<?>> types = feignClientBeanMeta.get(name);
            if (CollectionUtils.isEmpty(types)) {
                continue;
            }
            ConstructorArgumentValues.ValueHolder configsValueHolder = argumentValues.getIndexedArgumentValue(1, Class[].class);
            Object value = configsValueHolder.getValue();
            if (!(value instanceof Class[])) {
                continue;
            }
            Class<?>[] oldConfigs = (Class<?>[]) value;
            Set<Class<?>> newConfigs = Sets.newHashSetWithExpectedSize(ArrayUtils.getLength(oldConfigs) + annotationConfigMap.size());
            for (Class<?> aClass : types) {
                for (Map.Entry<Class<? extends Annotation>, Class<?>> entry : annotationConfigMap.entrySet()) {
                    Annotation annotation = AnnotationUtils.findAnnotation(aClass, entry.getKey());
                    if (null != annotation) {
                        newConfigs.add(entry.getValue());
                    }
                }
                FeignClient feignClient = AnnotationUtils.findAnnotation(aClass, FeignClient.class);
                if (null != feignClient && ArrayUtils.getLength(feignClient.configuration()) != 0) {
                    newConfigs.addAll(Arrays.asList(feignClient.configuration()));
                }
            }
            if (newConfigs.size() > 0) {
                Collections.addAll(newConfigs, oldConfigs);
                configsValueHolder.setValue(newConfigs.toArray(new Class[0]));
            }
        }
    }

    private Map<Class<? extends Annotation>, Class<?>> getAnnotationConfigMap() {
        if (CollectionUtils.isEmpty(annotationConfigHolders)) {
            return null;
        }
        Map<Class<? extends Annotation>, Class<?>> annotationConfigMap = new HashMap<>();
        for (AnnotationConfigHolder holder : annotationConfigHolders) {
            annotationConfigMap.put(holder.annotationType(), holder.configClass());
        }
        return annotationConfigMap;
    }

    private void scanFeignClientBeanDefinition(ConfigurableListableBeanFactory beanFactory) {
        String[] beanNames = beanFactory.getBeanNamesForAnnotation(FeignClient.class);
        for (String beanName : beanNames) {
            AbstractBeanDefinition beanDefinition = (AbstractBeanDefinition) beanFactory.getBeanDefinition(beanName);
            FeignClientFactoryBean feignClientFactoryBean = (FeignClientFactoryBean) beanDefinition.getAttribute("feignClientsRegistrarFactoryBean");
            String contextId = feignClientFactoryBean.getContextId();
            Class<?> typeClass = beanDefinition.getBeanClass();
            feignBeanMeta.put(beanName, Pair.of(typeClass, contextId));
            Method[] methods = typeClass.getMethods();
            for (Method method : methods) {
                String connectTimeoutKey = TimeoutKeyUtils.connectTimeoutKey(typeClass, method, contextId);
                String readTimeoutKey = TimeoutKeyUtils.readTimeoutKey(typeClass, method, contextId);
                log.info("use @FeignRequestOptions annotation on method connectTimeout key: {}", connectTimeoutKey);
                log.info("use @FeignRequestOptions annotation on method readTimeout key: {}", readTimeoutKey);
                FeignRequestOptions options = method.getAnnotation(FeignRequestOptions.class);
                if (null == options) {
                    continue;
                }
                int connectTimeout = options.connectTimeout();
                if (connectTimeout > 0) {
                    PropertySourceUtils.put(environment, connectTimeoutKey, connectTimeout);
                }
                int readTimeout = options.readTimeout();
                if (readTimeout > 0) {
                    PropertySourceUtils.put(environment, readTimeoutKey, readTimeout);
                }
            }
            FeignClientCustom feignClientCustom = AnnotationUtils.findAnnotation(typeClass, FeignClientCustom.class);
            if (null != feignClientCustom) {
                FeignPropertyUtils.process(feignClientCustom, environment, contextId, FeignClientCustom.class);
            }
        }
    }

    private ConfigurableEnvironment environment;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = (ConfigurableEnvironment) environment;
    }
}