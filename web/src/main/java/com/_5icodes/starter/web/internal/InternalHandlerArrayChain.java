package com._5icodes.starter.web.internal;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;

import java.util.List;

public class InternalHandlerArrayChain implements InternalHandlerChain {
    private final List<InternalHandler> internalHandlers;

    private int cur = -1;

    public InternalHandlerArrayChain(List<InternalHandler> internalHandlers) {
        this.internalHandlers = internalHandlers;
    }

    @Override
    public void advance(ChannelHandlerContext ctx, FullHttpRequest msg) {
        cur++;
        if (cur >= internalHandlers.size()) {
            return;
        }
        InternalHandler internalHandler = internalHandlers.get(cur);
        internalHandler.doHandle(ctx, msg, this);
    }
}