package com._5icodes.starter.common.utils;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;

import java.util.List;
import java.util.Map;

public class JsonUtils {
    private static final ObjectMapper objectMapper = SpringUtils.getBean(ObjectMapper.class);
    private static final ObjectMapper objectMapperWithOutLowerCase = objectMapper.copy().setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
    private static final ObjectMapper objectMapperIncludeTypeInfo = objectMapper.copy().setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
            //enableDefaultTyping方法已过期，在比较老的博客中，踩了坑用activateDefaultTyping替代
            //.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
            .activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.EVERYTHING, JsonTypeInfo.As.PROPERTY);

    public static String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("json transform error", e);
        }
    }

    public static byte[] toJsonBytes(Object obj) {
        try {
            return objectMapperIncludeTypeInfo.writeValueAsBytes(obj);
        } catch (Exception e) {
            throw new RuntimeException("json transform error", e);
        }
    }

    public static Object parseBytes(byte[] buffer) {
        try {
            return objectMapperIncludeTypeInfo.readValue(buffer, Object.class);
        } catch (Exception e) {
            throw new RuntimeException("json mapper error", e);
        }
    }

    /**
     * 转为指定类型对象
     *
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T parse(String json, Class<T> tClass) {
        return doParse(json, tClass, objectMapper);
    }

    /**
     * 转为指定类型list
     *
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> List<T> parseToList(String json, Class<T> tClass) {
        return doParseToList(json, tClass, objectMapper);
    }

    /**
     * 转为指定类型map
     *
     * @param json
     * @param keyType
     * @param valueType
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> parseToMap(String json, Class<K> keyType, Class<V> valueType) {
        return doParseToMap(json, keyType, valueType, objectMapper);
    }

    /**
     * 将有下划线的转换为驼峰字段的对象
     *
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> T parseSnakeCase(String json, Class<T> tClass) {
        return doParse(json, tClass, objectMapperWithOutLowerCase);
    }

    /**
     * 将有下划线的转换为驼峰字段的list
     *
     * @param json
     * @param tClass
     * @param <T>
     * @return
     */
    public static <T> List<T> parseToListSnakeCase(String json, Class<T> tClass) {
        return doParseToList(json, tClass, objectMapperWithOutLowerCase);
    }

    /**
     * 将有下划线的转换为驼峰字段的map
     *
     * @param json
     * @param keyType
     * @param valueType
     * @param <K>
     * @param <V>
     * @return
     */
    public static <K, V> Map<K, V> parseToMapSnakeCase(String json, Class<K> keyType, Class<V> valueType) {
        return doParseToMap(json, keyType, valueType, objectMapperWithOutLowerCase);
    }

    private static <T> T doParse(String json, Class<T> tClass, ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(json, tClass);
        } catch (Exception e) {
            throw new RuntimeException("json mapper error", e);
        }
    }

    private static <T> List<T> doParseToList(String json, Class<T> tClass, ObjectMapper objectMapper) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructParametricType(List.class, tClass);
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            throw new RuntimeException("json mapper error", e);
        }
    }

    private static <K, V> Map<K, V> doParseToMap(String json, Class<K> keyType, Class<V> valueType, ObjectMapper objectMapper) {
        try {
            JavaType javaType = objectMapper.getTypeFactory().constructMapType(Map.class, keyType, valueType);
            return objectMapper.readValue(json, javaType);
        } catch (Exception e) {
            throw new RuntimeException("json mapper error", e);
        }
    }
}