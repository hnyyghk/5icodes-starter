package com._5icodes.starter.eureka;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.lang.reflect.Field;
import java.util.stream.Stream;

@Slf4j
public class EurekaRefreshDisablePostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered {
    private MetadataReaderFactory metadataReaderFactory;

    public MetadataReaderFactory getMetadataReaderFactory() {
        return metadataReaderFactory;
    }

    public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
        this.metadataReaderFactory = metadataReaderFactory;
    }

    /**
     * 为了禁用EurekaClientConfigurationRefresher
     *
     * @param beanDefinitionRegistry
     * @throws BeansException
     * @see org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration.EurekaClientConfigurationRefresher
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanDefinitionRegistry) throws BeansException {
        try {
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(EurekaDiscoveryClientConfiguration.class.getName());
            ClassMetadata classMetadata = metadataReader.getClassMetadata();
            Field field = Class.forName("org.springframework.core.type.classreading.SimpleAnnotationMetadata").getDeclaredField("memberClassNames");
            field.setAccessible(true);
            String[] memberClassNames = (String[]) field.get(classMetadata);
            memberClassNames = ArrayUtils.removeElement(memberClassNames, "org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration$EurekaClientConfigurationRefresher");
            field.set(classMetadata, memberClassNames);
        } catch (Exception e) {
            log.error("disable org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration$EurekaClientConfigurationRefresher failed", e);
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        feignContextDestroyBugFix(beanFactory);
    }

    /**
     * 当关闭ApplicationContext时, 它将销毁所有单例bean, 首先销毁eurekaAutoServiceRegistration, 然后销毁feignContext
     * 当销毁feignContext时, 它将关闭与每个FeignClient相关联的ApplicationContext
     * 因为eurekaAutoServiceRegistration侦听ContextClosedEvent, 所以这些事件将被发送到该bean, 不幸的是, 因为它已经被销毁了
     * 所以将得到异常Error creating bean with name 'eurekaAutoServiceRegistration'
     *
     * @param beanFactory
     */
    private void feignContextDestroyBugFix(ConfigurableListableBeanFactory beanFactory) {
        boolean needSetDependOn = Stream.of("feignContext", "eurekaAutoServiceRegistration").allMatch(beanFactory::containsBean);
        if (needSetDependOn) {
            BeanDefinition feignContext = beanFactory.getBeanDefinition("feignContext");
            feignContext.setDependsOn("eurekaAutoServiceRegistration");
        }
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE - 1;
    }
}