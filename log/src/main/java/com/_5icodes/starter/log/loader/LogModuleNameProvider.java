package com._5icodes.starter.log.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.log.LogConstants;

public class LogModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return LogConstants.MODULE_NAME;
    }
}