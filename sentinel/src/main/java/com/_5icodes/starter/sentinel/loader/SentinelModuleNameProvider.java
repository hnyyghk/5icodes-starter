package com._5icodes.starter.sentinel.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.sentinel.SentinelConstants;

public class SentinelModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return SentinelConstants.MODULE_NAME;
    }
}