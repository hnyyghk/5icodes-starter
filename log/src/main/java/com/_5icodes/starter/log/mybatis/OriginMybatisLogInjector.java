package com._5icodes.starter.log.mybatis;

import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.support.SqlSessionDaoSupport;

import java.util.Optional;

public class OriginMybatisLogInjector extends AbstractMybatisLogInjector {
    @Override
    public Optional<Class<?>> getMapperInterface(SqlSessionDaoSupport bean) {
        if (bean instanceof MapperFactoryBean) {
            return Optional.of(((MapperFactoryBean<?>) bean).getMapperInterface());
        }
        return Optional.empty();
    }
}