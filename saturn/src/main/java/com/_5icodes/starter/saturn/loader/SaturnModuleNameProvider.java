package com._5icodes.starter.saturn.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.saturn.SaturnConstants;

public class SaturnModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return SaturnConstants.MODULE_NAME;
    }
}