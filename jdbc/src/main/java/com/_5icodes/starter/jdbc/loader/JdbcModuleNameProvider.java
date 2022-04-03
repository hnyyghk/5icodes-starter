package com._5icodes.starter.jdbc.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.jdbc.JdbcConstants;

public class JdbcModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return JdbcConstants.MODULE_NAME;
    }
}