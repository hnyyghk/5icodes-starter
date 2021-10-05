package com._5icodes.starter.web.internal;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface InternalHandler {
    void doHandle(ChannelHandlerContext ctx, FullHttpRequest msg, InternalHandlerChain chain);
}