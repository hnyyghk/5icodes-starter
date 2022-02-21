package com._5icodes.starter.webflux.monitor;

import com._5icodes.starter.monitor.ExceptionReport;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class ExceptionReportHandler implements ErrorWebExceptionHandler {
    private final ExceptionReport exceptionReport;

    public ExceptionReportHandler(ExceptionReport exceptionReport) {
        this.exceptionReport = exceptionReport;
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        try {
            ServerHttpRequest request = exchange.getRequest();
            exceptionReport.report("webflux:" + request.getPath(), ex);
        } catch (Exception ignored) {
        }
        return Mono.error(ex);
    }
}