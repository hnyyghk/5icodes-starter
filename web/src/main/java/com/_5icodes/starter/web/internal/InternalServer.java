package com._5icodes.starter.web.internal;

import com._5icodes.starter.common.infrastructure.AbstractSmartLifecycle;
import com._5icodes.starter.common.infrastructure.BootApplicationListener;
import com._5icodes.starter.web.WebProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
public class InternalServer extends AbstractSmartLifecycle implements BootApplicationListener<ApplicationReadyEvent> {
    private NioEventLoopGroup eventLoopGroup;

    private final List<InternalHandler> internalHandlers;

    private final int internalPort;

    private final String contextPath;

    public InternalServer(List<InternalHandler> internalHandlers, WebProperties properties) {
        this.internalHandlers = internalHandlers;
        this.internalPort = properties.getInternalPort();
        this.contextPath = properties.getContextPath();
    }

    @Override
    public void doStart() {
        log.info("InternalServer doStart");
    }

    @Override
    public void doStop() {
        if (eventLoopGroup != null) {
            eventLoopGroup.shutdownGracefully();
        }
    }

    @Override
    @SneakyThrows
    public void doOnApplicationEvent(ApplicationReadyEvent event) {
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
                                        if (StringUtils.hasText(contextPath)) {
                                            if (msg.uri().startsWith(contextPath + "/")) {
                                                msg.setUri(msg.uri().replace(contextPath, ""));
                                                InternalHandlerArrayChain arrayChain = new InternalHandlerArrayChain(internalHandlers);
                                                arrayChain.advance(ctx, msg);
                                            } else {
                                                log.info("wrong contextPath request is given up: {}", msg.uri());
                                                ctx.writeAndFlush(HttpResponseUtils.notFound());
                                            }
                                        } else {
                                            InternalHandlerArrayChain arrayChain = new InternalHandlerArrayChain(internalHandlers);
                                            arrayChain.advance(ctx, msg);
                                        }
                                    }
                                });
                    }
                });
        bootstrap.bind("localhost", internalPort).sync();
        if (StringUtils.hasText(contextPath)) {
            log.info("internal server start success with internalPort: {}, contextPath: {}", internalPort, contextPath);
        } else {
            log.info("internal server start success with internalPort: {}", internalPort);
        }
    }
}