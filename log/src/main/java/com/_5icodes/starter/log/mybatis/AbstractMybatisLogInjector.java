package com._5icodes.starter.log.mybatis;

import com._5icodes.starter.log.LogProperties;
import com._5icodes.starter.log.utils.Log4jUtils;
import org.apache.logging.log4j.Level;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.Map;
import java.util.Optional;

public abstract class AbstractMybatisLogInjector implements InitializingBean {
    @Autowired
    private LogProperties logProperties;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (!logProperties.isShowSql()) {
            return;
        }
        Map<String, SqlSessionDaoSupport> beans = applicationContext.getBeansOfType(SqlSessionDaoSupport.class);
        for (Map.Entry<String, SqlSessionDaoSupport> entry : beans.entrySet()) {
            SqlSessionDaoSupport bean = entry.getValue();
            getMapperInterface(bean).ifPresent(c -> {
                String name = c.getName();
                Log4jUtils.setLoggerLevel(name, Level.DEBUG, true);
            });
        }
    }

    public abstract Optional<Class<?>> getMapperInterface(SqlSessionDaoSupport bean);
}