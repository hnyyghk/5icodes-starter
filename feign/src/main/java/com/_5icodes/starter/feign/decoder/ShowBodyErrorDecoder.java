package com._5icodes.starter.feign.decoder;

import feign.Response;
import feign.Util;
import feign.codec.ErrorDecoder;

public class ShowBodyErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {
        byte[] bytes = null;
        try {
            Response.Body body = response.body();
            if (null != body && body.length() > 0) {
                bytes = Util.toByteArray(body.asInputStream());
            }
        } catch (Exception ignored) {
        }
        return new ErrorStatusFeignException(response.status(), bytes);
    }
}