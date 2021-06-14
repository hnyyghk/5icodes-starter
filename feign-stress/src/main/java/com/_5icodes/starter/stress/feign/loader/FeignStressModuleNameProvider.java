package com._5icodes.starter.stress.feign.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.stress.feign.FeignStressConstants;

public class FeignStressModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return FeignStressConstants.MODULE_NAME;
    }
}