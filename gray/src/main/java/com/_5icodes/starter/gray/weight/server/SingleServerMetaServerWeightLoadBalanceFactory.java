package com._5icodes.starter.gray.weight.server;

import com._5icodes.starter.gray.GrayConstants;
import com._5icodes.starter.gray.enums.ServerMetaEnum;
import com._5icodes.starter.gray.exception.ParseException;
import com._5icodes.starter.gray.utils.ServerUtils;
import com._5icodes.starter.gray.weight.ServerWeightLoadBalance;
import com._5icodes.starter.gray.weight.ServerWeightLoadBalanceFactory;
import com._5icodes.starter.gray.weight.WeightAlgorithm;
import com.google.common.math.DoubleMath;
import lombok.Data;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SingleServerMetaServerWeightLoadBalanceFactory implements ServerWeightLoadBalanceFactory<SingleServerMetaServerWeightLoadBalanceFactory.Config> {
    @Override
    public ServerWeightLoadBalance apply(Config config) throws ParseException {
        Map<String, Double> weights = config.getWeights();
        double left = calculateLeft(weights);
        ServerMetaEnum meta = config.getMeta();
        if (weights.size() == 1) {
            String metaKey = weights.keySet().iterator().next();
            if (DoubleMath.fuzzyEquals(left, 0D, GrayConstants.WEIGHT_TOLERANCE)) {
                return serverList -> {
                    List<ServiceInstance> servers = serverList.stream().filter(server -> meta.test(server, metaKey)).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(servers)) {
                        servers = serverList;
                    }
                    return ServerUtils.randomChoose(servers);
                };
            } else if (DoubleMath.fuzzyEquals(left, GrayConstants.MAX_WEIGHT, GrayConstants.WEIGHT_TOLERANCE)) {
                return serverList -> {
                    List<ServiceInstance> servers = serverList.stream().filter(server -> !meta.test(server, metaKey)).collect(Collectors.toList());
                    if (CollectionUtils.isEmpty(servers)) {
                        servers = serverList;
                    }
                    return ServerUtils.randomChoose(servers);
                };
            }
        }
        return serverList -> WeightAlgorithm.buildFromCandidates(serverList, server -> meta.getWeight(server, weights, left)).random();
    }

    private double calculateLeft(Map<String, Double> weights) throws ParseException {
        double total = 0D;
        for (Map.Entry<String, Double> entry : weights.entrySet()) {
            Double value = entry.getValue();
            if (value < 0) {
                throw new ParseException(String.format("weight of %s is %f, less than zero", entry.getKey(), value));
            }
            total += value;
        }
        if (total > GrayConstants.MAX_WEIGHT) {
            throw new ParseException(String.format("total weight %f is greater than 100.0", total));
        }
        return GrayConstants.MAX_WEIGHT - total;
    }

    @Data
    public static class Config {
        /**
         * VERSION/TAGS/REGION
         */
        private ServerMetaEnum meta;
        private Map<String, Double> weights;
    }
}