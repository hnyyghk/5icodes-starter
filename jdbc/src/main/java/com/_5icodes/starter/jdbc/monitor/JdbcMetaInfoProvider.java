package com._5icodes.starter.jdbc.monitor;

import com._5icodes.starter.jdbc.JdbcConstants;
import com._5icodes.starter.monitor.meta.AbstractMetaInfoProvider;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.Map;

public class JdbcMetaInfoProvider extends AbstractMetaInfoProvider {
    @Override
    protected Map<String, Object> doGetMetaInfo(ApplicationStartedEvent event) {
        Map<String, Object> result = new HashMap<>();
        Map<String, String> typeInfos = DatasourceTypeInfoRegistry.getInstance().getTypeInfos();
        if (!CollectionUtils.isEmpty(typeInfos)) {
            result.put(JdbcConstants.JDBC_META, typeInfos);
        }
        return result;
    }
}