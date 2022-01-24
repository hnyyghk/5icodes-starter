package com._5icodes.starter.web.internal;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DumpHandler implements InternalHandler {
    @Override
    public void doHandle(ChannelHandlerContext ctx, FullHttpRequest msg, InternalHandlerChain chain) {
        log.info("wrong uri request is given up: {}", msg.uri());
        ctx.writeAndFlush(HttpResponseUtils.notFound());
    }
}