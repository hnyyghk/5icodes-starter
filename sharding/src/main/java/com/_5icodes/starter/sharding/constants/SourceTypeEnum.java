package com._5icodes.starter.sharding.constants;

public enum SourceTypeEnum {
    MASTER_ONLY(1),
    MASTER_SLAVE(2),
    SHARDING(3),
    ;

    private final Integer code;

    SourceTypeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}