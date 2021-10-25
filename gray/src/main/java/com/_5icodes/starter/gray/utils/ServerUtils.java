package com._5icodes.starter.gray.utils;

import com._5icodes.starter.gray.enums.ServerMetaEnum;
import lombok.experimental.UtilityClass;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@UtilityClass
public class ServerUtils {
    public Set<String> get(ServiceInstance server, String key) {
        Map<String, String> metadata = server.getMetadata();
        if (CollectionUtils.isEmpty(metadata)) {
            return Collections.emptySet();
        }
        String value = metadata.get(key);
        if (StringUtils.isEmpty(value)) {
            return Collections.emptySet();
        }
        return StringUtils.commaDelimitedListToSet(value);
    }

    public String set(ServiceInstance server, String key, String value) {
        Map<String, String> metadata = server.getMetadata();
        if (CollectionUtils.isEmpty(metadata)) {
            return null;
        }
        return metadata.put(key, value);
    }

    public ServiceInstance randomChoose(List<ServiceInstance> candidates) {
        if (candidates == null) {
            return null;
        }
        int size = candidates.size();
        if (size == 0) {
            return null;
        } else if (size == 1) {
            return candidates.get(0);
        } else {
            return candidates.get(ThreadLocalRandom.current().nextInt(size));
        }
    }

    public String getServerStr(List<ServiceInstance> list) {
        return list.stream().map(server -> server.getHost() + "|" + server.getPort() + "|" + get(server, ServerMetaEnum.ZONE.getName())).collect(Collectors.joining(","));
    }
}