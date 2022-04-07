package com._5icodes.starter.sharding.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于启动多数据源或者sharding使用
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Import(ShardingDataSourceRegister.class)
public @interface EnableShardingSource {
    /**
     * sharding配置前缀，不包含每个数据库的名称配置
     */
    String prefix() default "";

    /**
     * 数据源配置
     */
    EachSource[] sources();
}