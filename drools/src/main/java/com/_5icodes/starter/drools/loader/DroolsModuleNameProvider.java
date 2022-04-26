package com._5icodes.starter.drools.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.drools.DroolsConstants;

public class DroolsModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return DroolsConstants.MODULE_NAME;
    }
}