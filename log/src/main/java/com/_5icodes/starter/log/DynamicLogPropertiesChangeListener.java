package com._5icodes.starter.log;

import com._5icodes.starter.log.converter.CustomDynamicMaxLengthConverter;
import com._5icodes.starter.log.converter.CustomExtendedThrowablePatternConverter;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScopeRefreshedEvent;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;

public class DynamicLogPropertiesChangeListener implements SmartApplicationListener {
    private final LogProperties logProperties;

    public DynamicLogPropertiesChangeListener(LogProperties logProperties) {
        this.logProperties = logProperties;
    }

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return RefreshScopeRefreshedEvent.class.isAssignableFrom(eventType) || ApplicationReadyEvent.class.isAssignableFrom(eventType);
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        CustomDynamicMaxLengthConverter instance = CustomDynamicMaxLengthConverter.getInstance();
        if (instance != null) {
            instance.setMaxLength(logProperties.getMaxLength());
            instance.setBigLogEnable(logProperties.isBigLogEnable());
        }
        CustomExtendedThrowablePatternConverter converter = CustomExtendedThrowablePatternConverter.getInstance();
        if (converter != null) {
            converter.setLineNum(logProperties.getLineNum());
        }
    }
}