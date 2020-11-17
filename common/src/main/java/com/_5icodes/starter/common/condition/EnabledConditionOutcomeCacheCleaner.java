package com._5icodes.starter.common.condition;

import com._5icodes.starter.common.infrastructure.BootApplicationListener;
import org.springframework.boot.context.event.ApplicationStartedEvent;

public class EnabledConditionOutcomeCacheCleaner implements BootApplicationListener<ApplicationStartedEvent> {
    @Override
    public void doOnApplicationEvent(ApplicationStartedEvent event) {
        SimpleMapEnabledConditionOutcomeCache.getInstance().clear();
    }
}