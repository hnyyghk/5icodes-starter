package com._5icodes.starter.gray.parser;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.gray.config.ConfigurableFactory;
import com._5icodes.starter.gray.exception.ParseException;

import java.util.List;
import java.util.Map;

public class DelegateJsonParser<T> implements JsonParser<T> {
    private final List<ConfigurableFactory<?, T>> delegates;

    public DelegateJsonParser(List<ConfigurableFactory<?, T>> delegates) {
        this.delegates = delegates;
    }

    @Override
    public T parse(Map<String, String> map) throws ParseException {
        String type = map.get(TYPE_KEY);
        for (ConfigurableFactory delegate : delegates) {
            if (delegate.shortName().equals(type)) {
                Class aClass = delegate.configClass();
                String configStr = map.get(CONFIG_KEY);
                Object config = JsonUtils.parse(configStr, aClass);
                return (T) delegate.apply(config);
            }
        }
        throw new ParseException(String.format("jsonParser for type %s is not found.", type));
    }
}