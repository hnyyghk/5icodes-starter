package com._5icodes.starter.common.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.common.CommonConstants;

public class CommonModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return CommonConstants.MODULE_NAME;
    }
}