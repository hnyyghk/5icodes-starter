package com._5icodes.starter.feign.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.feign.FeignConstants;

public class FeignModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return FeignConstants.MODULE_NAME;
    }
}