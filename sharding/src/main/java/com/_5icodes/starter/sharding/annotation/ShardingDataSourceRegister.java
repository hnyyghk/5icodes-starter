package com._5icodes.starter.sharding.annotation;

import com._5icodes.starter.sharding.ShardingDataSourceFactoryBean;
import com._5icodes.starter.sharding.constants.SourceTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import tk.mybatis.spring.mapper.ClassPathMapperScanner;
import tk.mybatis.spring.mapper.MapperFactoryBean;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ShardingDataSourceRegister implements ImportBeanDefinitionRegistrar, ResourceLoaderAware, EnvironmentAware, PriorityOrdered {
    private static final String DATA_SOURCE = "dataSource";
    private static final String DATA_SOURCE_NAME = "DataSource";
    private static final String SQL_SESSION_FACTORY = "SqlSessionFactory";
    private static final String SQL_SESSION_TEMPLATE = "SqlSessionTemplate";
    private Environment environment;
    private ResourceLoader resourceLoader;

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(EnableShardingSource.class.getName()));
        if (annotationAttributes == null) {
            return;
        }
        String prefix = annotationAttributes.getString("prefix");
        AnnotationAttributes[] sources = (AnnotationAttributes[]) annotationAttributes.get("sources");
        for (AnnotationAttributes source : sources) {
            String dbPrefix = source.getString("dbPrefix");
            String typeAliasesPackage = source.getString("typeAliasesPackage");
            String[] mapperLocations = source.getStringArray("mapperLocations");
            SourceTypeEnum sourceType = source.getEnum("sourceType");
            String[] basePackages = source.getStringArray("value");
            createAndRegisterMapperBean(registry, sourceType.getCode(), prefix, dbPrefix, mapperLocations, typeAliasesPackage, basePackages, source);
        }
    }

    private void createAndRegisterMapperBean(BeanDefinitionRegistry registry, Integer sourceType, String prefix, String dbPrefix, String[] mapperLocations, String typeAliasesPackage, String[] basePackages, AnnotationAttributes source) {
        //创建DataSource数据结构
        createAndRegisterDataSource(registry, sourceType, prefix, dbPrefix);

        //创建JdbcTemplate数据结构
        createAndRegisterJdbcTemplate(registry, dbPrefix);

        //创建SqlSessionFactory数据结构
        createAndRegisterSqlSessionFactory(registry, dbPrefix, mapperLocations, typeAliasesPackage);

        //创建DataSourceTransaction数据结构
        createAndRegisterTransaction(registry, dbPrefix);

        //创建SqlSessionTemplate数据结构
        createAndRegisterSqlSessionTemplate(registry, dbPrefix);

        //创建TkMapperScanner数据结构
        createTkMapperScanner(registry, dbPrefix, source);
    }

    /**
     * @see tk.mybatis.spring.annotation.MapperScannerRegistrar#registerBeanDefinitions(AnnotationMetadata, BeanDefinitionRegistry)
     * @see org.mybatis.spring.annotation.MapperScannerRegistrar#registerBeanDefinitions(AnnotationMetadata, BeanDefinitionRegistry)
     */
    private void createTkMapperScanner(BeanDefinitionRegistry registry, String dbPrefix, AnnotationAttributes annoAttrs) {
        ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
        // this check is needed in Spring 3.1
        if (resourceLoader != null) {
            scanner.setResourceLoader(resourceLoader);
        }

        Class<? extends Annotation> annotationClass = annoAttrs.getClass("annotationClass");
        if (!Annotation.class.equals(annotationClass)) {
            scanner.setAnnotationClass(annotationClass);
        }

        Class<?> markerInterface = annoAttrs.getClass("markerInterface");
        if (!Class.class.equals(markerInterface)) {
            scanner.setMarkerInterface(markerInterface);
        }

        Class<? extends BeanNameGenerator> generatorClass = annoAttrs.getClass("nameGenerator");
        if (!BeanNameGenerator.class.equals(generatorClass)) {
            scanner.setBeanNameGenerator(BeanUtils.instantiateClass(generatorClass));
        }

        Class<? extends MapperFactoryBean> mapperFactoryBeanClass = annoAttrs.getClass("factoryBean");
        if (!MapperFactoryBean.class.equals(mapperFactoryBeanClass)) {
            scanner.setMapperFactoryBean(BeanUtils.instantiateClass(mapperFactoryBeanClass));
        }

        scanner.setSqlSessionTemplateBeanName(dbPrefix + SQL_SESSION_TEMPLATE);
        scanner.setSqlSessionFactoryBeanName(dbPrefix + SQL_SESSION_FACTORY);

        List<String> basePackages = new ArrayList<String>();
        for (String pkg : annoAttrs.getStringArray("value")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (String pkg : annoAttrs.getStringArray("basePackages")) {
            if (StringUtils.hasText(pkg)) {
                basePackages.add(pkg);
            }
        }
        for (Class<?> clazz : annoAttrs.getClassArray("basePackageClasses")) {
            basePackages.add(ClassUtils.getPackageName(clazz));
        }
        //优先级 mapperHelperRef > properties > springboot
        String mapperHelperRef = annoAttrs.getString("mapperHelperRef");
        String[] properties = annoAttrs.getStringArray("properties");
        if (StringUtils.hasText(mapperHelperRef)) {
            scanner.setMapperHelperBeanName(mapperHelperRef);
        } else if (properties != null && properties.length > 0) {
            scanner.setMapperProperties(properties);
        } else {
            try {
                scanner.setMapperProperties(this.environment);
            } catch (Exception e) {
                log.warn("只有 Spring Boot 环境中可以通过 Environment(配置文件,环境变量,运行参数等方式) 配置通用 Mapper，" +
                    "其他环境请通过 @MapperScan 注解中的 mapperHelperRef 或 properties 参数进行配置!" +
                    "如果你使用 tk.mybatis.mapper.session.Configuration 配置的通用 Mapper，你可以忽略该错误!", e);
            }
        }
        scanner.registerFilters();
        scanner.doScan(StringUtils.toStringArray(basePackages));
    }

    private void createAndRegisterSqlSessionTemplate(BeanDefinitionRegistry registry, String dbPrefix) {
        BeanDefinitionBuilder sqlSessionTemplateBuilder = BeanDefinitionBuilder.genericBeanDefinition(SqlSessionTemplate.class);
        sqlSessionTemplateBuilder.addConstructorArgReference(dbPrefix + SQL_SESSION_FACTORY);
        registry.registerBeanDefinition(dbPrefix + SQL_SESSION_TEMPLATE, sqlSessionTemplateBuilder.getBeanDefinition());
    }

    private void createAndRegisterTransaction(BeanDefinitionRegistry registry, String dbPrefix) {
        BeanDefinitionBuilder transactionBuilder = BeanDefinitionBuilder.genericBeanDefinition(DataSourceTransactionManager.class);
        transactionBuilder.setLazyInit(false);
        transactionBuilder.addPropertyReference(DATA_SOURCE, dbPrefix + DATA_SOURCE_NAME);
        registry.registerBeanDefinition(dbPrefix + "Transaction", transactionBuilder.getBeanDefinition());
    }

    private void createAndRegisterSqlSessionFactory(BeanDefinitionRegistry registry, String dbPrefix, String[] mapperLocations, String typeAliasesPackage) {
        BeanDefinitionBuilder sqlSessionBuilder = BeanDefinitionBuilder.genericBeanDefinition(SqlSessionFactoryBean.class);
        sqlSessionBuilder.setLazyInit(false);
        sqlSessionBuilder.addPropertyReference(DATA_SOURCE, dbPrefix + DATA_SOURCE_NAME);
        if (StringUtils.hasText(typeAliasesPackage)) {
            sqlSessionBuilder.addPropertyValue("typeAliasesPackage", typeAliasesPackage);
        }
        //如果没有mapperLocations，则需要xml文件和mapper文件放在一起
        if (mapperLocations.length > 0) {
            sqlSessionBuilder.addPropertyValue("mapperLocations", mapperLocations);
        }
        registry.registerBeanDefinition(dbPrefix + SQL_SESSION_FACTORY, sqlSessionBuilder.getBeanDefinition());
    }

    private void createAndRegisterJdbcTemplate(BeanDefinitionRegistry registry, String dbPrefix) {
        BeanDefinitionBuilder jdbcBuilder = BeanDefinitionBuilder.genericBeanDefinition(JdbcTemplate.class);
        jdbcBuilder.setLazyInit(false);
        jdbcBuilder.addPropertyReference(DATA_SOURCE, dbPrefix + DATA_SOURCE_NAME);
        registry.registerBeanDefinition(dbPrefix + "JdbcTemplate", jdbcBuilder.getBeanDefinition());
    }

    private void createAndRegisterDataSource(BeanDefinitionRegistry registry, Integer sourceType, String prefix, String dbPrefix) {
        BeanDefinitionBuilder dataSourceBuilder = BeanDefinitionBuilder.genericBeanDefinition(ShardingDataSourceFactoryBean.class);
        dataSourceBuilder.setLazyInit(false);
        dataSourceBuilder.addPropertyValue("envPrefix", StringUtils.hasText(prefix) ? prefix + "." + dbPrefix : dbPrefix);
        dataSourceBuilder.addPropertyValue("environment", environment);
        dataSourceBuilder.addPropertyValue("sourceType", sourceType);
        registry.registerBeanDefinition(dbPrefix + DATA_SOURCE_NAME, dataSourceBuilder.getBeanDefinition());
    }

    @Override
    public int getOrder() {
        return Integer.MIN_VALUE;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }
}