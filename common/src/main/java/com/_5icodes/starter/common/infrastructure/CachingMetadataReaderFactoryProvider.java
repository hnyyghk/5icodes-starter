package com._5icodes.starter.common.infrastructure;

import com._5icodes.starter.common.utils.SpringApplicationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.type.classreading.ConcurrentReferenceCachingMetadataReaderFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

@Slf4j
public class CachingMetadataReaderFactoryProvider implements ApplicationListener<ContextRefreshedEvent> {
    private ConcurrentReferenceCachingMetadataReaderFactory metadataReaderFactory;

    private ResourcePatternResolver resourcePatternResolver;

    private volatile boolean cleared = false;

    private final ApplicationContext applicationContext;

    public CachingMetadataReaderFactoryProvider(ApplicationContext context) throws Exception {
        applicationContext = context;
        init();
    }

    private void init() throws Exception {
        try {
            metadataReaderFactory = (ConcurrentReferenceCachingMetadataReaderFactory) applicationContext.getAutowireCapableBeanFactory().getBean(
                    BEAN_NAME,
                    MetadataReaderFactory.class
            );
        } catch (Exception e) {
            metadataReaderFactory = new ConcurrentReferenceCachingMetadataReaderFactory(applicationContext);
        }
        resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(applicationContext);
        List<String> packages = SpringApplicationUtils.getBasePackages((ConfigurableEnvironment) applicationContext.getEnvironment());
        List<Resource> resources = getResourcesFromPackages(packages);
        distinctProcessResources(resources);
    }

    private void distinctProcessResources(List<Resource> resources) throws IOException {
        Set<String> scannedRes = new HashSet<>();
        for (Resource resource : resources) {
            String uriStr = getUriString(resource);
            try {
                if (!scannedRes.add(uriStr)) {
                    continue;
                }
                doProcessResource(resource);
            } catch (IOException e) {
                log.warn("process metadata reader on resource {} failed", uriStr, e);
            }
        }
    }

    private void doProcessResource(Resource resource) throws IOException {
        if (resource.isReadable()) {
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
            String className = metadataReader.getClassMetadata().getClassName();
            CACHE.put(className, metadataReader);
        }
    }

    private final Map<String, MetadataReader> CACHE = new HashMap<>();

    private String getUriString(Resource resource) throws IOException {
        return resource.getURI().toString();
    }

    private List<Resource> getResourcesFromPackages(List<String> packages) throws IOException {
        List<Resource> resources = new ArrayList<>();
        for (String basePackages : packages) {
            String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    resolveBasePackage(basePackages) + "/**/*.class";
            resources.addAll(Arrays.asList(resourcePatternResolver.getResources(packageSearchPath)));
        }
        return resources;
    }

    private String resolveBasePackage(String basePackages) {
        return ClassUtils.convertClassNameToResourcePath(applicationContext.getEnvironment().resolveRequiredPlaceholders(basePackages));
    }

    /**
     * @see org.springframework.boot.autoconfigure.SharedMetadataReaderFactoryContextInitializer
     */
    private static final String BEAN_NAME = "org.springframework.boot.autoconfigure."
            + "internalCachingMetadataReaderFactory";

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (null != metadataReaderFactory && !cleared && applicationContext == event.getApplicationContext()) {
            metadataReaderFactory.clearCache();
            CACHE.clear();
            cleared = true;
        }
    }

    public MetadataReader getMetadataReader(String className) {
        return CACHE.get(className);
    }

    public Map<String, MetadataReader> getAllMetadataReader() {
        return CACHE;
    }

    public void processMetadataReader(Consumer<MetadataReader> consumer) {
        for (Map.Entry<String, MetadataReader> entry : CACHE.entrySet()) {
            consumer.accept(entry.getValue());
        }
    }
}