package com._5icodes.starter.log.mybatis;

import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import tk.mybatis.spring.mapper.MapperFactoryBean;

import java.util.Optional;

@Configuration
@ConditionalOnClass(MapperFactoryBean.class)
public class TkMybatisLogInjector extends AbstractMybatisLogInjector {
    @Override
    public Optional<Class<?>> getMapperInterface(SqlSessionDaoSupport bean) {
        if (bean instanceof MapperFactoryBean) {
            return Optional.of(((MapperFactoryBean<?>) bean).getMapperInterface());
        }
        return Optional.empty();
    }
}