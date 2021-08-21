package com._5icodes.starter.sentinel;

import com._5icodes.starter.common.infrastructure.BootApplicationListener;
import com.alibaba.csp.sentinel.log.CommandCenterLog;
import com.alibaba.csp.sentinel.log.RecordLog;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import org.slf4j.bridge.SLF4JBridgeHandler;
import org.springframework.boot.context.event.ApplicationStartedEvent;

import java.io.File;
import java.lang.reflect.Field;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.FileHandler;
import java.util.logging.Handler;

public class ChangeLogApplicationListener implements BootApplicationListener<ApplicationStartedEvent> {
    @Override
    public void doOnApplicationEvent(ApplicationStartedEvent event) {
        doWithSentinelLog(RecordLog.class);
        doWithSentinelLog(CommandCenterLog.class);
        doWithFlowRuleManager();
    }

    private void doWithSentinelLog(Class<?> clazz) {
        FileHandler fileHandler = null;
        Handler oldHandler = null;
        try {
            Class<?> dateFileLogHandlerClass = Class.forName("com.alibaba.csp.sentinel.log.DateFileLogHandler");
            Field logHandlerField = clazz.getDeclaredField("logHandler");
            logHandlerField.setAccessible(true);
            oldHandler = (Handler) logHandlerField.get(null);
            if (!dateFileLogHandlerClass.isInstance(oldHandler)) {
                return;
            }
            oldHandler.close();
            Field fileHandlerField = dateFileLogHandlerClass.getDeclaredField("handler");
            fileHandlerField.setAccessible(true);
            fileHandler = (FileHandler) fileHandlerField.get(oldHandler);
            Field filesField = FileHandler.class.getDeclaredField("files");
            filesField.setAccessible(true);
            File[] files = (File[]) filesField.get(fileHandler);
            for (File file : files) {
                file.delete();
            }
            logHandlerField.set(null, new SLF4JBridgeHandler());
        } catch (NoSuchFieldException | ClassNotFoundException | IllegalAccessException e) {
            throw new IllegalStateException("process sentinel logs failed", e);
        } finally {
            if (fileHandler != null) {
                fileHandler.close();
            }
            if (oldHandler != null) {
                oldHandler.close();
            }
        }
    }

    private void doWithFlowRuleManager() {
        try {
            Field schedulerField = FlowRuleManager.class.getDeclaredField("SCHEDULER");
            schedulerField.setAccessible(true);
            ScheduledExecutorService scheduledExecutorService = (ScheduledExecutorService) schedulerField.get(null);
            if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
                scheduledExecutorService.shutdownNow();
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("process flowRuleManager scheduler failed", e);
        }
    }
}