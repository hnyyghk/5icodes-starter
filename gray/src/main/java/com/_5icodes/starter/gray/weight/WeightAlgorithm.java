package com._5icodes.starter.gray.weight;

import lombok.AllArgsConstructor;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Function;

@AllArgsConstructor
public class WeightAlgorithm<T> {
    private final TreeMap<Double, T> weightMap;

    public static <T> WeightAlgorithm<T> buildFromCandidates(List<T> candidates, Function<T, Double> weightFunc) {
        TreeMap<Double, T> weightMap = new TreeMap<>();
        for (T candidate : candidates) {
            Double weight = weightFunc.apply(candidate);
            if (weight <= 0) {
                continue;
            }
            double lastWeight = weightMap.size() == 0 ? 0 : weightMap.lastKey();
            weightMap.put(weight + lastWeight, candidate);
        }
        return new WeightAlgorithm<>(weightMap);
    }

    public T random() {
        double randomWeight = weightMap.lastKey() * Math.random();
        NavigableMap<Double, T> tailMap = weightMap.tailMap(randomWeight, false);
        return weightMap.get(tailMap.firstKey());
    }
}