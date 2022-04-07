package com._5icodes.starter.log.monitor;

import com._5icodes.starter.common.infrastructure.BootApplicationListener;
import com._5icodes.starter.log.converter.CustomDynamicMaxLengthConverter;
import com._5icodes.starter.log.converter.CustomExtendedThrowablePatternConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;

public class LogMetricStartListener implements BootApplicationListener<ApplicationReadyEvent> {
    @Override
    public void doOnApplicationEvent(ApplicationReadyEvent event) {
        CustomExtendedThrowablePatternConverter instance = CustomExtendedThrowablePatternConverter.getInstance();
        if (instance != null) {
            instance.setPreTimeMillis(System.currentTimeMillis());
        }
        CustomDynamicMaxLengthConverter.setStartTime(System.currentTimeMillis());
    }
}