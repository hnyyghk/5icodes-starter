package com._5icodes.starter.feign;

import com._5icodes.starter.common.application.ApplicationRunListenerAdapter;
import com._5icodes.starter.common.utils.AnnotationChangeUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * 为bootApplicationClass增加@EnableFeignClients注解
 */
public class FeignAnnotationListener extends ApplicationRunListenerAdapter {
    public FeignAnnotationListener(SpringApplication application, String[] args) {
        super(application, args);
    }

    @Override
    protected void doStarting() {
        Class<?> bootApplicationClass = SpringApplicationUtils.getBootApplicationClass(getApplication());
        EnableFeignClients enableFeignClients = bootApplicationClass.getDeclaredAnnotation(EnableFeignClients.class);
        if (null == enableFeignClients) {
            List<String> packages = SpringApplicationUtils.getBasePackages(getApplication());
            final String[] packagesArray = packages.toArray(new String[0]);
            AnnotationChangeUtils.addAnnotation(bootApplicationClass, EnableFeignClients.class, new EnableFeignClients() {
                @Override
                public Class<? extends Annotation> annotationType() {
                    return EnableFeignClients.class;
                }

                @Override
                public String[] value() {
                    return new String[0];
                }

                @Override
                public String[] basePackages() {
                    return packagesArray;
                }

                @Override
                public Class<?>[] basePackageClasses() {
                    return new Class[0];
                }

                @Override
                public Class<?>[] defaultConfiguration() {
                    return new Class[0];
                }

                @Override
                public Class<?>[] clients() {
                    return new Class[0];
                }
            });
        }
        super.doStarting();
    }
}