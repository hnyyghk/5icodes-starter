package com._5icodes.starter.monitor.meta;

import java.util.List;

public interface MetaInfoSender {
    void send(List<MetaInfoProvider> metaInfoProviders);
}