package com._5icodes.starter.redisson.loader;

import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.redisson.RedissonConstants;

public class RedissonModuleNameProvider implements ModuleNameProvider {
    @Override
    public String getModuleName() {
        return RedissonConstants.MODULE_NAME;
    }
}