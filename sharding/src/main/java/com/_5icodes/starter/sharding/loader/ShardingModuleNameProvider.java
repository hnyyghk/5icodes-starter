package com._5icodes.starter.sharding.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.sharding.ShardingConstants;

public class ShardingModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return ShardingConstants.MODULE_NAME;
    }
}