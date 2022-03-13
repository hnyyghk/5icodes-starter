package com._5icodes.starter.async.loader;

import com._5icodes.starter.async.AsyncConstants;
import com._5icodes.starter.common.version.ModuleNameProvider;

public class AsyncModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return AsyncConstants.MODULE_NAME;
    }
}