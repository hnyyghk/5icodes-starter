package com._5icodes.starter.kafka.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.kafka.KafkaConstants;

public class KafkaModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return KafkaConstants.MODULE_NAME;
    }
}