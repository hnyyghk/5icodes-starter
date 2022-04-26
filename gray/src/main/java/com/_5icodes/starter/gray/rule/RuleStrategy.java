package com._5icodes.starter.gray.rule;

import com._5icodes.starter.gray.SleuthStrategyContext;
import com._5icodes.starter.gray.utils.ServerChooseUtils;
import com._5icodes.starter.gray.utils.ServerUtils;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;

public interface RuleStrategy {
    /**
     * 在过滤之后从剩余的服务器列表中选择
     *
     * @param candidates
     * @return
     */
    default ServiceInstance choose(List<ServiceInstance> candidates) {
        //优先匹配灰度请求，其次再分组请求筛选
        if (SleuthStrategyContext.get()) {
            SleuthStrategyContext.remove();
        } else {
            candidates = ServerChooseUtils.chooseServer(candidates);
        }
        return ServerUtils.randomChoose(candidates);
    }
}