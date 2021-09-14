package com._5icodes.starter.webflux.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.webflux.WebFluxConstants;

public class WebFluxModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return WebFluxConstants.MODULE_NAME;
    }
}