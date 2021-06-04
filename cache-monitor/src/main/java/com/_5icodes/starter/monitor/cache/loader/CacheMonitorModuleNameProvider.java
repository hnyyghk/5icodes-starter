package com._5icodes.starter.monitor.cache.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.monitor.cache.CacheMonitorConstants;

public class CacheMonitorModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return CacheMonitorConstants.MODULE_NAME;
    }
}