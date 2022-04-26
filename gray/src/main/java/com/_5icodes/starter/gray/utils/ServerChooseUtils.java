package com._5icodes.starter.gray.utils;

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
        //判断当前请求是否是分组请求
        if (StringUtils.hasText(getGroupValue())) {
            //如果是分组请求，返回对应分组实例
            return candidates.stream().filter(server -> ServerMetaEnum.APP_GROUP.test(server, getGroupValue())).collect(Collectors.toList());
        } else {
            //如果不是分组请求，返回无分组实例
            return candidates.stream().filter(server -> ServerUtils.get(server, ServerMetaEnum.APP_GROUP.getMetaName()).isEmpty()).collect(Collectors.toList());
        }
    }

    /**
     * 获取分组请求标识
     *
     * @return
     */
    private String getGroupValue() {
        String group = BaggageFieldUtils.get(ServerMetaEnum.APP_GROUP.getMetaName());
        return StringUtils.hasText(group) ? group.toUpperCase() : null;
    }
}