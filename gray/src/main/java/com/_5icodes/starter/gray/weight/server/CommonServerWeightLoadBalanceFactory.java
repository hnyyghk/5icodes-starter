package com._5icodes.starter.gray.weight.server;

import com._5icodes.starter.gray.GrayConstants;
import com._5icodes.starter.gray.exception.ParseException;
import com._5icodes.starter.gray.parser.JsonParser;
import com._5icodes.starter.gray.server.ServerPredicate;
import com._5icodes.starter.gray.utils.ServerUtils;
import com._5icodes.starter.gray.weight.ServerWeightLoadBalance;
import com._5icodes.starter.gray.weight.ServerWeightLoadBalanceFactory;
import com._5icodes.starter.gray.weight.WeightAlgorithm;
import com.google.common.math.DoubleMath;
import lombok.Data;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CommonServerWeightLoadBalanceFactory implements ServerWeightLoadBalanceFactory<CommonServerWeightLoadBalanceFactory.Config> {
    private final JsonParser<ServerPredicate> serverPredicateJsonParser;

    public CommonServerWeightLoadBalanceFactory(JsonParser<ServerPredicate> serverPredicateJsonParser) {
        this.serverPredicateJsonParser = serverPredicateJsonParser;
    }

    @Override
    public ServerWeightLoadBalance apply(Config config) throws ParseException {
        List<Pair<ServerPredicate, Double>> weightPairs = new ArrayList<>();
        double total = 0D;
        for (WeightKeyValue weightKeyValue : config.getWeights()) {
            Map<String, String> map = weightKeyValue.getServer();
            ServerPredicate serverPredicate = serverPredicateJsonParser.parse(map);
            double weight = weightKeyValue.getWeight();
            if (weight < 0) {
                throw new ParseException(String.format("weight %f is less than 0", weight));
            }
            weightPairs.add(Pair.of(serverPredicate, weight));
            total += weight;
        }
        if (total > GrayConstants.MAX_WEIGHT) {
            throw new ParseException(String.format("total weight %f is greater than 100.0", total));
        } else if (DoubleMath.fuzzyEquals(total, GrayConstants.MAX_WEIGHT, GrayConstants.WEIGHT_TOLERANCE) && weightPairs.size() == 1) {
            ServerPredicate serverPredicate = weightPairs.get(0).getLeft();
            return serverList -> ServerUtils.randomChoose(serverList.stream().filter(serverPredicate::test).collect(Collectors.toList()));
        } else if (total == 0 && weightPairs.size() == 1) {
            ServerPredicate serverPredicate = weightPairs.get(0).getLeft();
            return serverList -> ServerUtils.randomChoose(serverList.stream().filter(server -> !serverPredicate.test(server)).collect(Collectors.toList()));
        }
        double left = GrayConstants.MAX_WEIGHT - total;
        return serverList -> WeightAlgorithm.buildFromCandidates(serverList,
                server -> {
                    for (Pair<ServerPredicate, Double> pair : weightPairs) {
                        if (pair.getLeft().test(server)) {
                            return pair.getRight();
                        }
                    }
                    return left;
                }).random();
    }

    @Data
    public static class Config {
        private List<WeightKeyValue> weights;
    }

    @Data
    public static class WeightKeyValue {
        private Map<String, String> server;
        private double weight;
    }
}