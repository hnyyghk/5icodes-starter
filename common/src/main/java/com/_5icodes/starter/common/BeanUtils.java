package com._5icodes.starter.common;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;

@UtilityClass
public class BeanUtils {
    @SneakyThrows
    public <T> void mergeBean(T srcObj, T desObj) {
        Class<?> type = srcObj.getClass();
        PropertyDescriptor[] propertyDescriptors = org.springframework.beans.BeanUtils.getPropertyDescriptors(type);
        for (PropertyDescriptor descriptor : propertyDescriptors) {
            String propertyName = descriptor.getName();
            if ("class".equals(propertyName)) {
                continue;
            }
            Method readMethod = descriptor.getReadMethod();
            Method writeMethod = descriptor.getWriteMethod();
            if (null != readMethod && null != writeMethod) {
                Object result = readMethod.invoke(srcObj);
                if (result != null) {
                    writeMethod.invoke(desObj, result);
                }
            }
        }
    }
}