package com._5icodes.starter.monitor.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.monitor.MonitorConstants;

public class MonitorModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return MonitorConstants.MODULE_NAME;
    }
}