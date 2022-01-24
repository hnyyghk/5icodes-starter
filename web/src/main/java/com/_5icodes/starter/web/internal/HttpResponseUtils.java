package com._5icodes.starter.web.internal;

import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import lombok.experimental.UtilityClass;

@UtilityClass
public class HttpResponseUtils {
    public DefaultFullHttpResponse ok() {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, 0);
        return response;
    }

    public DefaultFullHttpResponse notFound() {
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        response.headers().add(HttpHeaderNames.CONTENT_LENGTH, 0);
        return response;
    }
}