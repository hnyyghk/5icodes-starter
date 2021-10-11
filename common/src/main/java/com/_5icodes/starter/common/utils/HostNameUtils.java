package com._5icodes.starter.common.utils;

import lombok.experimental.UtilityClass;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

@UtilityClass
public class HostNameUtils {
    private static String hostAddress;
    private static String hostName;

    static {
        try {
            resolveHost();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void resolveHost() throws Exception {
        InetAddress localHost = InetAddress.getLocalHost();
        hostName = localHost.getHostName();
        hostAddress = localHost.getHostAddress();
        if (localHost.isLoopbackAddress()) {
            //find the first IPv4 Address that not loopback
            for (Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces(); networkInterfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();
                for (Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses(); inetAddresses.hasMoreElements(); ) {
                    InetAddress inetAddress = inetAddresses.nextElement();
                    if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                        hostAddress = inetAddress.getHostAddress();
                    }
                }
            }
        }
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public String getHostName() {
        return hostName;
    }
}