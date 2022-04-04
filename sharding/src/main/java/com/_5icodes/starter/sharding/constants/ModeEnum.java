package com._5icodes.starter.sharding.constants;

public enum ModeEnum {
    AVG(1),
    SINGLE(2),
    ;

    private final Integer code;

    ModeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}