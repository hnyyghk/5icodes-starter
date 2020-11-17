package com._5icodes.starter.stress.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.stress.StressConstants;

public class StressModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return StressConstants.MODULE_NAME;
    }
}