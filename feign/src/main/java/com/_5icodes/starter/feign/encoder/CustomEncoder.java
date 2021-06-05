package com._5icodes.starter.feign.encoder;

import feign.RequestTemplate;
import feign.codec.EncodeException;

import java.lang.reflect.Type;

public interface CustomEncoder {
    boolean encode(Object object, Type bodyType, RequestTemplate template) throws EncodeException;
}