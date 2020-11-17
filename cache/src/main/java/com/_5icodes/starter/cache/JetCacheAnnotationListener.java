package com._5icodes.starter.cache;

import com._5icodes.starter.common.application.ApplicationRunListenerAdapter;
import com._5icodes.starter.common.utils.AnnotationChangeUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com.alicp.jetcache.anno.config.EnableCreateCacheAnnotation;
import com.alicp.jetcache.anno.config.EnableMethodCache;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.core.Ordered;

import java.lang.annotation.Annotation;
import java.util.List;

public class JetCacheAnnotationListener extends ApplicationRunListenerAdapter {
    public JetCacheAnnotationListener(SpringApplication application, String[] args) {
        super(application, args);
    }

    @Override
    protected void doStarting() {
        Class<?> bootApplicationClass = SpringApplicationUtils.getBootApplicationClass(getApplication());
        EnableCreateCacheAnnotation enableCreateCacheAnnotation = bootApplicationClass.getDeclaredAnnotation(EnableCreateCacheAnnotation.class);
        if (null == enableCreateCacheAnnotation) {
            AnnotationChangeUtils.addAnnotation(bootApplicationClass, EnableCreateCacheAnnotation.class, new EnableCreateCacheAnnotation() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return EnableCreateCacheAnnotation.class;
                }
            });
        }
        EnableMethodCache enableMethodCache = bootApplicationClass.getDeclaredAnnotation(EnableMethodCache.class);
        if (null == enableMethodCache) {
            List<String> packages = SpringApplicationUtils.getBasePackages(getApplication());
            final String[] packagesArray = packages.toArray(new String[0]);
            AnnotationChangeUtils.addAnnotation(bootApplicationClass, EnableMethodCache.class, new EnableMethodCache() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return EnableMethodCache.class;
                }

                @Override
                public boolean proxyTargetClass() {
                    return false;
                }

                @Override
                public AdviceMode mode() {
                    return AdviceMode.PROXY;
                }

                @Override
                public int order() {
                    return Ordered.LOWEST_PRECEDENCE;
                }

                @Override
                public String[] basePackages() {
                    return packagesArray;
                }
            });
        }
        super.doStarting();
    }
}