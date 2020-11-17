package com._5icodes.starter.stress.cache.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.stress.cache.CacheStressConstants;

public class CacheStressModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return CacheStressConstants.MODULE_NAME;
    }
}