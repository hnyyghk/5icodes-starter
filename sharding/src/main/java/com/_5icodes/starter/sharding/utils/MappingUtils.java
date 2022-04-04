package com._5icodes.starter.sharding.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class MappingUtils {
    public String createFormat(int length) {
        if (length <= 0) {
            return "";
        }
        return "%0" + length + "d";
    }
}