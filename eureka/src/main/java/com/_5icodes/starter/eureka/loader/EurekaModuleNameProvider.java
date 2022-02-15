package com._5icodes.starter.eureka.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.eureka.EurekaConstants;

public class EurekaModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return EurekaConstants.MODULE_NAME;
    }
}