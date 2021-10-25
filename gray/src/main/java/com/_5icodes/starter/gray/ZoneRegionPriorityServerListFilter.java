package com._5icodes.starter.gray;

import com._5icodes.starter.common.utils.SpringUtils;
import com._5icodes.starter.gray.enums.ServerMetaEnum;
import com._5icodes.starter.gray.utils.ServerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.config.LoadBalancerZoneConfig;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.cloud.loadbalancer.core.ZonePreferenceServiceInstanceListSupplier;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

import java.util.*;

@Slf4j
public class ZoneRegionPriorityServerListFilter extends ZonePreferenceServiceInstanceListSupplier {
    private final RegionZoneMetaProvider metaProvider;

    public ZoneRegionPriorityServerListFilter(ServiceInstanceListSupplier delegate, LoadBalancerZoneConfig zoneConfig) {
        super(delegate, zoneConfig);
        metaProvider = SpringUtils.getBean(RegionZoneMetaProvider.class);
    }

    @Override
    public Flux<List<ServiceInstance>> get() {
        String zone = metaProvider.getZone();
        String region = metaProvider.getRegion();
        log.debug("metadata zone: {} region: {}", zone, region);
        if (zone == null || region == null) {
            return super.get();
        }
        return getDelegate().get().map(this::getFilteredListOfServers);
    }

    private List<ServiceInstance> getFilteredListOfServers(List<ServiceInstance> output) {
        List<ServiceInstance> filteredServers = new LinkedList<>();
        List<ServiceInstance> neighbourServers = new LinkedList<>();
        List<ServiceInstance> remoteServers = new LinkedList<>();
        String zone = metaProvider.getZone();
        String region = metaProvider.getRegion();
        for (ServiceInstance server : output) {
            Set<String> valueList = ServerUtils.get(server, ServerMetaEnum.ZONE.getName());
            String serverZone;
            if (valueList.isEmpty()) {
                //EurekaClientConfigBean.DEFAULT_ZONE
                serverZone = "defaultZone";
                ServerUtils.set(server, ServerMetaEnum.ZONE.getName(), serverZone);
            } else {
                serverZone = valueList.iterator().next();
            }
            log.debug("server: {}", ServerUtils.getServerStr(Collections.singletonList(server)));
            if (Objects.equals(serverZone, zone)) {
                filteredServers.add(server);
                log.debug("add to filteredServers");
            } else {
                String serverRegion = metaProvider.getRegionByZone(serverZone);
                log.debug("region: {}", serverRegion);
                if (Objects.equals(region, serverRegion)) {
                    neighbourServers.add(server);
                    log.debug("add to neighbourServers");
                } else {
                    remoteServers.add(server);
                    log.debug("add to remoteServers");
                }
            }
        }
        if (CollectionUtils.isEmpty(filteredServers)) {
            filteredServers = neighbourServers;
            if (CollectionUtils.isEmpty(filteredServers)) {
                filteredServers = remoteServers;
            }
        }
        log.debug("before filter: {}", ServerUtils.getServerStr(output));
        log.debug("after filter: {}", ServerUtils.getServerStr(filteredServers));
        return filteredServers;
    }
}
