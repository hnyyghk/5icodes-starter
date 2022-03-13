package com._5icodes.starter.async.policy;

import java.time.Duration;

/**
 * 0为不延时,后面每个级别的延时messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
 */
public enum DelayTimeLevel {
    NO_DELAY(Duration.ZERO),
    S_1(Duration.ofSeconds(1)),
    S_5(Duration.ofSeconds(5)),
    S_10(Duration.ofSeconds(10)),
    S_30(Duration.ofSeconds(30)),
    M_1(Duration.ofMinutes(1)),
    M_2(Duration.ofMinutes(2)),
    M_3(Duration.ofMinutes(3)),
    M_4(Duration.ofMinutes(4)),
    M_5(Duration.ofMinutes(5)),
    M_6(Duration.ofMinutes(6)),
    M_7(Duration.ofMinutes(7)),
    M_8(Duration.ofMinutes(8)),
    M_9(Duration.ofMinutes(9)),
    M_10(Duration.ofMinutes(10)),
    M_20(Duration.ofMinutes(20)),
    M_30(Duration.ofMinutes(30)),
    H_1(Duration.ofHours(1)),
    H_2(Duration.ofHours(2)),
    ;

    private Duration duration;

    DelayTimeLevel(Duration duration) {
        this.duration = duration;
    }

    public Duration getDuration() {
        return duration;
    }
}