package com._5icodes.starter.web.internal;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

public interface InternalHandlerChain {
    void advance(ChannelHandlerContext ctx, FullHttpRequest msg);
}