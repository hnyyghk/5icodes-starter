package com._5icodes.starter.sleuth.utils;

import brave.baggage.BaggageField;
import lombok.experimental.UtilityClass;

import java.util.Map;

@UtilityClass
public class BaggageFieldUtils {
    public String get(String name) {
        BaggageField baggageField = BaggageField.getByName(name);
        return baggageField == null ? null : baggageField.getValue();
    }

    public Map<String, String> getAll() {
        return BaggageField.getAllValues();
    }

    public void set(String name, String value) {
        BaggageField baggageField = BaggageField.getByName(name);
        if (baggageField == null) {
            baggageField = BaggageField.create(name);
        }
        baggageField.updateValue(value);
    }
}