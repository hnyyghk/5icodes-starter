package com._5icodes.starter.jdbc.trace;

import com._5icodes.starter.common.BeanUtils;
import com._5icodes.starter.jdbc.JdbcProperties;
import com._5icodes.starter.stress.StressConstants;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;

import javax.management.ObjectName;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;

@Slf4j
public class TraceTestDatasourcePostProcessor implements BeanPostProcessor {
    private static final Class druidDatasourceWrapperClass;
    private static final Constructor constructor;

    static {
        try {
            druidDatasourceWrapperClass = ClassUtils.forName("com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceWrapper", ClassUtils.getDefaultClassLoader());
            constructor = druidDatasourceWrapperClass.getDeclaredConstructor();
            constructor.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final JdbcProperties jdbcProperties;

    public TraceTestDatasourcePostProcessor(JdbcProperties jdbcProperties) {
        this.jdbcProperties = jdbcProperties;
    }

    @Override
    @SneakyThrows
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (druidDatasourceWrapperClass.isInstance(bean) && !CollectionUtils.isEmpty(jdbcProperties.getTraceTestMap())) {
            DruidDataSource originalDataSource = (DruidDataSource) bean;
            String originalDataSourceName = originalDataSource.getName();
            DruidDataSource druidDataSource = jdbcProperties.getTraceTestMap().get(originalDataSourceName);
            if (druidDataSource == null) {
                return bean;
            }
            DruidDataSource traceTestDataSource = (DruidDataSource) constructor.newInstance();
            BeanUtils.mergeBean(originalDataSource, traceTestDataSource);
            BeanUtils.mergeBean(druidDataSource, traceTestDataSource);
            String traceTestDataSourceName = traceTestDataSource.getName();
            if (traceTestDataSourceName.equals(originalDataSourceName)) {
                traceTestDataSourceName = originalDataSourceName + StressConstants.DB_SUFFIX;
                traceTestDataSource.setName(traceTestDataSourceName);
            }
            log.info("original datasource: {} url: {}", originalDataSourceName, originalDataSource.getUrl());
            log.info("trace test datasource: {} url: {}", traceTestDataSourceName, traceTestDataSource.getUrl());
            AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
                ObjectName objectName = DruidDataSourceStatManager.addDataSource(traceTestDataSource, traceTestDataSource.getName());
                traceTestDataSource.setObjectName(objectName);
                return null;
            });
            return new TraceTestDatasource(originalDataSource, traceTestDataSource);
        }
        return bean;
    }
}