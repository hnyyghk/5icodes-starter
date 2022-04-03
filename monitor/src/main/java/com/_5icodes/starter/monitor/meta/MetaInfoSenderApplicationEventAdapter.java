package com._5icodes.starter.monitor.meta;

import com._5icodes.starter.common.infrastructure.BootApplicationListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;

import java.util.List;

public class MetaInfoSenderApplicationEventAdapter implements BootApplicationListener<ApplicationReadyEvent> {
    private final List<MetaInfoProvider> metaInfoProviders;
    private final MetaInfoSender metaInfoSender;

    public MetaInfoSenderApplicationEventAdapter(List<MetaInfoProvider> metaInfoProviders, MetaInfoSender metaInfoSender) {
        this.metaInfoProviders = metaInfoProviders;
        this.metaInfoSender = metaInfoSender;
    }

    @Override
    public void doOnApplicationEvent(ApplicationReadyEvent event) {
        metaInfoSender.send(metaInfoProviders);
    }
}