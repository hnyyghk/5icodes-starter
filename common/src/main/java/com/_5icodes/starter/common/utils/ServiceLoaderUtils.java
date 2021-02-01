package com._5icodes.starter.common.utils;

import lombok.experimental.UtilityClass;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ServiceLoader;

@UtilityClass
public class ServiceLoaderUtils {
    public <T> T loadFirst(Class<T> tClass) {
        Iterator<T> iterator = loadAll(tClass);
        return iterator.next();
    }

    public <T> Iterator<T> loadAll(Class<T> tClass) {
        Iterator<T> iterator = loadAllIfPresent(tClass);
        if (!iterator.hasNext()) {
            throw new IllegalStateException(String.format(
                    "No implementation defined in /META-INF/service/%s, please check whether the file exists and has the right implementation class!",
                    tClass.getName()
            ));
        }
        return iterator;
    }

    public <T> Iterator<T> loadAllIfPresent(Class<T> tClass) {
        ServiceLoader<T> loader = ServiceLoader.load(tClass);
        return loader.iterator();
    }

    public <T> T loadByOrder(Class<T> tClass) {
        Iterator<T> iterator = loadAll(tClass);
        LinkedList<T> instances = new LinkedList<>();
        while (iterator.hasNext()) {
            instances.add(iterator.next());
        }
        AnnotationAwareOrderComparator.sort(instances);
        return instances.getFirst();
    }
}