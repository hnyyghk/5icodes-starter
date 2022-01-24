package com._5icodes.starter.web.internal;

import com.netflix.discovery.EurekaClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;

@Slf4j
public class GracefulShutdownHandler implements InternalHandler, Ordered {
    private final EurekaClient discoveryClient;

    private static final String SHUTDOWN_URI = "/shutdown";

    public GracefulShutdownHandler(EurekaClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    public void doHandle(ChannelHandlerContext ctx, FullHttpRequest msg, InternalHandlerChain chain) {
        if (SHUTDOWN_URI.equals(msg.uri())) {
            log.info("GracefulShutdownHandler doHandle");
            discoveryClient.shutdown();
            ctx.writeAndFlush(HttpResponseUtils.ok());
        } else {
            chain.advance(ctx, msg);
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }
}