package com._5icodes.starter.gray.utils;

import com._5icodes.starter.gray.GrayConstants;
import com._5icodes.starter.gray.enums.ServerMetaEnum;
import com._5icodes.starter.sleuth.utils.BaggageFieldUtils;
import lombok.experimental.UtilityClass;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 节点分组灰度
 */
@UtilityClass
public class ServerChooseUtils {
    /**
     * 按照优先级选择服务
     *
     * @param candidates
     * @return
     */
    public List<ServiceInstance> chooseServer(List<ServiceInstance> candidates) {
        List<ServiceInstance> filterServer;
        if (isGroupRequest()) {
            filterServer = candidates.stream().filter(ServerChooseUtils::isFirstPriorityServer).collect(Collectors.toList());
            if (filterServer.isEmpty()) {
                filterServer = candidates.stream().filter(ServerChooseUtils::isSecondPriorityServer).collect(Collectors.toList());
            }
            return filterServer.isEmpty() ? candidates : filterServer;
        }
        filterServer = candidates.stream()
                .filter(server -> !isSecondPriorityServer(server))
                .filter(server -> !ServerUtils.get(server, ServerMetaEnum.TAGS.getName()).contains(GrayConstants.GRAY_TAG))
                .collect(Collectors.toList());
        return filterServer.isEmpty() ? candidates : filterServer;
    }

    /**
     * 判断当前请求是否是分组请求
     *
     * @return
     */
    private boolean isGroupRequest() {
        return StringUtils.hasText(getGroupValue());
    }

    /**
     * 获取分组请求标识
     *
     * @return
     */
    private String getGroupValue() {
        String group = BaggageFieldUtils.get(ServerMetaEnum.APP_GROUP.getName());
        return StringUtils.hasText(group) ? group.toUpperCase() : null;
    }

    /**
     * 是否是第一优先级服务
     *
     * @param server
     * @return
     */
    private boolean isFirstPriorityServer(ServiceInstance server) {
        return ServerUtils.get(server, ServerMetaEnum.APP_GROUP.getName()).contains(getGroupValue());
    }

    /**
     * 是否是第二优先级服务
     *
     * @param server
     * @return
     */
    private boolean isSecondPriorityServer(ServiceInstance server) {
        return !ServerUtils.get(server, ServerMetaEnum.APP_GROUP.getName()).isEmpty();
    }
}