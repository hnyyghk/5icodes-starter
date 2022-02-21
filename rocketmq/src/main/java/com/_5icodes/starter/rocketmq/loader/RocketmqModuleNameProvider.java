package com._5icodes.starter.rocketmq.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.rocketmq.RocketmqConstants;

public class RocketmqModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return RocketmqConstants.MODULE_NAME;
    }
}