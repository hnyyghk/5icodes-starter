package com._5icodes.starter.cache.test;

import cn.hutool.core.net.NetUtil;
import com._5icodes.starter.common.infrastructure.AbstractSmartLifecycle;
import org.springframework.beans.factory.BeanInitializationException;
import redis.embedded.RedisServer;

import java.io.File;
import java.lang.reflect.Field;
import java.util.List;

public class EmbeddedRedisServer extends AbstractSmartLifecycle {
    private Process process;

    private final Integer port;

    private final Integer maxHeap;

    public EmbeddedRedisServer(Integer port, Integer maxHeap) {
        this.port = port;
        this.maxHeap = maxHeap;
        start();
    }

    private List<String> resolveArgs() throws Exception {
        RedisServer redisServer = RedisServer.builder().port(port).build();
        Class<?> aClass = Class.forName("redis.embedded.AbstractRedisInstance");
        Field argsField = aClass.getDeclaredField("args");
        argsField.setAccessible(true);
        List<String> args = (List) argsField.get(redisServer);
        if (maxHeap != null && maxHeap > 0) {
            args.add("--maxheap");
            args.add(String.valueOf(maxHeap * 1024 * 1014));
        }
        return args;
    }

    @Override
    public void doStart() {
        if (!NetUtil.isUsableLocalPort(port)) {
            return;
        }
        if (process != null) {
            return;
        }
        try {
            List<String> args = resolveArgs();
            File executable = new File(args.get(0));
            ProcessBuilder processBuilder = new ProcessBuilder(args);
            processBuilder.directory(executable.getParentFile());
            processBuilder.redirectError(ProcessBuilder.Redirect.INHERIT);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            process = processBuilder.start();
        } catch (Exception e) {
            throw new BeanInitializationException("EmbeddedRedisServer start failed", e);
        }
    }

    @Override
    public void doStop() {
        if (process != null && process.isAlive()) {
            process.destroy();
        }
    }

    @Override
    public int getPhase() {
        return Integer.MIN_VALUE;
    }
}