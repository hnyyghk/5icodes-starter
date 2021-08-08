package com._5icodes.starter.web.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.web.WebConstants;

public class WebModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return WebConstants.MODULE_NAME;
    }
}