package com._5icodes.starter.webmvc.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.webmvc.WebMvcConstants;

public class WebMvcModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return WebMvcConstants.MODULE_NAME;
    }
}