package com._5icodes.starter.monitor.meta;

import com._5icodes.starter.common.infrastructure.BootApplicationListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.util.List;

public class MetaInfoSenderApplicationEventAdapter implements BootApplicationListener<ApplicationReadyEvent> {
    @Autowired(required = false)
    private List<MetaInfoProvider> metaInfoProviders;
    @Autowired
    private MetaInfoSender metaInfoSender;

    @Override
    public void doOnApplicationEvent(ApplicationReadyEvent event) {
        metaInfoSender.send(metaInfoProviders);
    }
}