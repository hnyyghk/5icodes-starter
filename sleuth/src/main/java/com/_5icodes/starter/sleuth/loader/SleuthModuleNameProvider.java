package com._5icodes.starter.sleuth.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.sleuth.SleuthConstants;

public class SleuthModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return SleuthConstants.MODULE_NAME;
    }
}