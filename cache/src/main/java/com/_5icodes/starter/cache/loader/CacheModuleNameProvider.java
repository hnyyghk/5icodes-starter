package com._5icodes.starter.cache.loader;

import com._5icodes.starter.cache.CacheConstants;
import com._5icodes.starter.common.version.ModuleNameProvider;

public class CacheModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return CacheConstants.MODULE_NAME;
    }
}