package com._5icodes.starter.apollo.loader;

import com._5icodes.starter.apollo.ApolloConstants;
import com._5icodes.starter.common.version.ModuleNameProvider;

public class ApolloModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return ApolloConstants.MODULE_NAME;
    }
}