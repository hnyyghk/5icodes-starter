package com._5icodes.starter.jasypt.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.jasypt.JasyptConstants;

public class JasyptModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return JasyptConstants.MODULE_NAME;
    }
}