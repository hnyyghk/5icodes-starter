package com._5icodes.starter.common.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;

@Slf4j
public class IpUtils {
    private volatile static String hostAddress;

    @SneakyThrows
    public static String getHostAddress() {
        if (hostAddress == null) {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        }
        return hostAddress;
    }

    //todo
}