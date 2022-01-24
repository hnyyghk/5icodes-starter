package com._5icodes.starter.web.internal;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;

@Slf4j
public class HealthProbeHandler implements InternalHandler, Ordered {
    private static final String PROBE_URI = "/health";

    @Override
    public void doHandle(ChannelHandlerContext ctx, FullHttpRequest msg, InternalHandlerChain chain) {
        if (PROBE_URI.equals(msg.uri())) {
            log.info("HealthProbeHandler doHandle");
            ctx.writeAndFlush(HttpResponseUtils.ok());
        } else {
            chain.advance(ctx, msg);
        }
    }

    @Override
    public int getOrder() {
        return -1;
    }
}