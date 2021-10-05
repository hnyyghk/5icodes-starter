package com._5icodes.starter.web.internal;

import com._5icodes.starter.common.infrastructure.AbstractSmartLifecycle;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;

import java.util.List;

@Slf4j
public class InternalServer extends AbstractSmartLifecycle {
    private NioEventLoopGroup eventLoopGroup;

    private final List<InternalHandler> internalHandlers;

    private final int port;

    public InternalServer(List<InternalHandler> internalHandlers, int port) {
        this.internalHandlers = internalHandlers;
        this.port = port;
    }

    @Override
    public void doStart() {
        AnnotationAwareOrderComparator.sort(internalHandlers);
        ServerBootstrap bootstrap = new ServerBootstrap();
        eventLoopGroup = new NioEventLoopGroup(1);
        bootstrap.group(eventLoopGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("http-server-codec", new HttpServerCodec())
                                .addLast("http-aggregator", new HttpObjectAggregator(1024 * 1024))
                                .addLast(new SimpleChannelInboundHandler<FullHttpRequest>() {
                                    @Override
                                    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
                                        InternalHandlerArrayChain arrayChain = new InternalHandlerArrayChain(internalHandlers);
                                        arrayChain.advance(ctx, msg);
                                    }
                                });
                    }
                });
        bootstrap.bind("localhost", port).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                log.info("hook server start success with port: {}", port);
            } else {
                log.warn("hook server start failed with port: {}", port, future.cause());
            }
        });
    }

    @Override
    public void doStop() {
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
    }
}