package com._5icodes.starter.gray.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.gray.GrayConstants;

public class GrayModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return GrayConstants.MODULE_NAME;
    }
}