package com._5icodes.starter.log.mybatis;

import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnClass(MapperFactoryBean.class)
@Import(TkMybatisLogInjector.class)
public class MybatisLogConfiguration {
    @Bean
    public static OriginMybatisLogInjector originMybatisLogInjector() {
        return new OriginMybatisLogInjector();
    }
}