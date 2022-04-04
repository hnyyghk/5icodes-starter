package com._5icodes.starter.jdbc.monitor;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * @see com.alibaba.druid.filter.stat.StatFilter
 * @see com.alibaba.druid.wall.WallFilter
 */
public class DruidFilterInitBeanPostProcessor implements BeanPostProcessor {
    /**
     * 这里的时机必须为before设置filter上面，原因在afterPropertySet方法会执行初始化操作
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DruidDataSource) {
            DruidDataSourceFilterProcessor.getInstance().addFilters((DruidDataSource) bean);
        }
        return bean;
    }
}