/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com._5icodes.starter.monitor.cache.monitor;

/**
 * Represents metrics data in a period of time span.
 *
 * @author jialiang.linjl
 * @author Eric Zhao
 */
public class CacheMetricBucket {

    private final LongAdder[] counters;

    private volatile long minRt;

    public CacheMetricBucket() {
        CacheMetricEvent[] events = CacheMetricEvent.values();
        this.counters = new LongAdder[events.length];
        for (CacheMetricEvent event : events) {
            counters[event.ordinal()] = new LongAdder();
        }
        initMinRt();
    }

    public CacheMetricBucket reset(CacheMetricBucket bucket) {
        for (CacheMetricEvent event : CacheMetricEvent.values()) {
            counters[event.ordinal()].reset();
            counters[event.ordinal()].add(bucket.get(event));
        }
        initMinRt();
        return this;
    }

    private void initMinRt() {
        this.minRt = CacheSentinelConfig.DEFAULT_STATISTIC_MAX_RT;
    }

    /**
     * Reset the adders.
     *
     * @return new metric bucket in initial state
     */
    public CacheMetricBucket reset() {
        for (CacheMetricEvent event : CacheMetricEvent.values()) {
            counters[event.ordinal()].reset();
        }
        initMinRt();
        return this;
    }

    public long get(CacheMetricEvent event) {
        return counters[event.ordinal()].sum();
    }

    public CacheMetricBucket add(CacheMetricEvent event, long n) {
        counters[event.ordinal()].add(n);
        return this;
    }

    public long pass() {
        return get(CacheMetricEvent.PASS);
    }

    public long occupiedPass() {
        return get(CacheMetricEvent.OCCUPIED_PASS);
    }

    public long block() {
        return get(CacheMetricEvent.BLOCK);
    }

    public long exception() {
        return get(CacheMetricEvent.EXCEPTION);
    }

    public long rt() {
        return get(CacheMetricEvent.RT);
    }

    public long minRt() {
        return minRt;
    }

    public long success() {
        return get(CacheMetricEvent.SUCCESS);
    }

    public void addPass(int n) {
        add(CacheMetricEvent.PASS, n);
    }

    public void addOccupiedPass(int n) {
        add(CacheMetricEvent.OCCUPIED_PASS, n);
    }

    public void addException(int n) {
        add(CacheMetricEvent.EXCEPTION, n);
    }

    public void addBlock(int n) {
        add(CacheMetricEvent.BLOCK, n);
    }

    public void addSuccess(int n) {
        add(CacheMetricEvent.SUCCESS, n);
    }

    public void addRT(long rt) {
        add(CacheMetricEvent.RT, rt);

        // Not thread-safe, but it's okay.
        if (rt < minRt) {
            minRt = rt;
        }
    }

    @Override
    public String toString() {
        return "p: " + pass() + ", b: " + block() + ", w: " + occupiedPass();
    }
}