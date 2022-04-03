package com._5icodes.starter.monitor.spi;

import com._5icodes.starter.common.utils.ServiceLoaderUtils;
import com._5icodes.starter.common.version.ModuleNameProvider;
import com._5icodes.starter.monitor.meta.AbstractMetaInfoProvider;
import com.google.common.collect.Maps;
import org.springframework.boot.context.event.ApplicationStartedEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ModulesMetaInfoProvider extends AbstractMetaInfoProvider {
    @Override
    protected Map<String, Object> doGetMetaInfo(ApplicationStartedEvent event) {
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(1);
        List<String> modules = new ArrayList<>();
        Iterator<ModuleNameProvider> moduleNameProviderIterator = ServiceLoaderUtils.loadAll(ModuleNameProvider.class);
        while (moduleNameProviderIterator.hasNext()) {
            ModuleNameProvider moduleNameProvider = moduleNameProviderIterator.next();
            modules.add(moduleNameProvider.getModuleName());
        }
        result.put("modules", modules);
        return result;
    }
}