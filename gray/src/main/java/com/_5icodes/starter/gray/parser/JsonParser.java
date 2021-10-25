package com._5icodes.starter.gray.parser;

import com._5icodes.starter.gray.exception.ParseException;

import java.util.Map;

public interface JsonParser<T> {
    String TYPE_KEY = "type";

    String CONFIG_KEY = "config";

    T parse(Map<String, String> map) throws ParseException;
}