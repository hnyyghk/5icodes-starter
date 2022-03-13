package com._5icodes.starter.async.annotation;

public enum Mode {
    /**
     * 异步模式，调用方法会发送数据到消息队列，并立即返回
     */
    ASYNC,
    /**
     * 保护模式，调用方法会发送数据到消息队列，并继续执行方法
     * 用于异步确保方法被完全执行，方法需幂等
     */
    PROTECTION,
    /**
     * 失败重试模式，仅在调用方法失败后发送数据到消息队列
     */
    FAIL_RETRY
}