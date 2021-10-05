package com._5icodes.starter.swagger.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.swagger.SwaggerConstants;

public class SwaggerModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return SwaggerConstants.MODULE_NAME;
    }
}