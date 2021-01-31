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

import com._5icodes.starter.common.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * The basic metric class in Sentinel using a {@link CacheBucketLeapArray} internal.
 *
 * @author jialiang.linjl
 * @author Eric Zhao
 */
public class CacheArrayMetric {

    private final CacheLeapArray<CacheMetricBucket> data;

    public CacheArrayMetric(int sampleCount, int intervalInMs) {
        this.data = new CacheBucketLeapArray(sampleCount, intervalInMs);
    }

    /**
     * For unit test.
     */
    public CacheArrayMetric(CacheLeapArray<CacheMetricBucket> array) {
        this.data = array;
    }

    public long success() {
        data.currentWindow();
        long success = 0;

        List<CacheMetricBucket> list = data.values();
        for (CacheMetricBucket window : list) {
            success += window.success();
        }
        return success;
    }

    public long maxSuccess() {
        data.currentWindow();
        long success = 0;

        List<CacheMetricBucket> list = data.values();
        for (CacheMetricBucket window : list) {
            if (window.success() > success) {
                success = window.success();
            }
        }
        return Math.max(success, 1);
    }

    public long exception() {
        data.currentWindow();
        long exception = 0;
        List<CacheMetricBucket> list = data.values();
        for (CacheMetricBucket window : list) {
            exception += window.exception();
        }
        return exception;
    }

    public long block() {
        data.currentWindow();
        long block = 0;
        List<CacheMetricBucket> list = data.values();
        for (CacheMetricBucket window : list) {
            block += window.block();
        }
        return block;
    }

    public long pass() {
        data.currentWindow();
        long pass = 0;
        List<CacheMetricBucket> list = data.values();

        for (CacheMetricBucket window : list) {
            pass += window.pass();
        }
        return pass;
    }

    public long occupiedPass() {
        data.currentWindow();
        long pass = 0;
        List<CacheMetricBucket> list = data.values();
        for (CacheMetricBucket window : list) {
            pass += window.occupiedPass();
        }
        return pass;
    }

    public long rt() {
        data.currentWindow();
        long rt = 0;
        List<CacheMetricBucket> list = data.values();
        for (CacheMetricBucket window : list) {
            rt += window.rt();
        }
        return rt;
    }

    public long minRt() {
        data.currentWindow();
        long rt = CacheSentinelConfig.DEFAULT_STATISTIC_MAX_RT;
        List<CacheMetricBucket> list = data.values();
        for (CacheMetricBucket window : list) {
            if (window.minRt() < rt) {
                rt = window.minRt();
            }
        }

        return Math.max(1, rt);
    }

    public List<CacheMetricNode> details() {
        List<CacheMetricNode> details = new ArrayList<>();
        data.currentWindow();
        List<CacheWindowWrap<CacheMetricBucket>> list = data.list();
        for (CacheWindowWrap<CacheMetricBucket> window : list) {
            if (window == null) {
                continue;
            }

            details.add(fromBucket(window));
        }

        return details;
    }

    public List<CacheMetricNode> detailsOnCondition(Predicate<Long> timePredicate) {
        List<CacheMetricNode> details = new ArrayList<>();
        data.currentWindow();
        List<CacheWindowWrap<CacheMetricBucket>> list = data.list();
        for (CacheWindowWrap<CacheMetricBucket> window : list) {
            if (window == null) {
                continue;
            }
            if (timePredicate != null && !timePredicate.test(window.windowStart())) {
                continue;
            }

            details.add(fromBucket(window));
        }

        return details;
    }

    private CacheMetricNode fromBucket(CacheWindowWrap<CacheMetricBucket> wrap) {
        CacheMetricNode node = new CacheMetricNode();
        node.setBlockQps(wrap.value().block());
        node.setExceptionQps(wrap.value().exception());
        node.setPassQps(wrap.value().pass());
        long successQps = wrap.value().success();
        node.setSuccessQps(successQps);
        if (successQps != 0) {
            node.setRt(wrap.value().rt() / successQps);
        } else {
            node.setRt(wrap.value().rt());
        }
        node.setTimestamp(wrap.windowStart());
        node.setOccupiedPassQps(wrap.value().occupiedPass());
        return node;
    }

    public CacheMetricBucket[] windows() {
        data.currentWindow();
        return data.values().toArray(new CacheMetricBucket[0]);
    }

    public void addException(int count) {
        CacheWindowWrap<CacheMetricBucket> wrap = data.currentWindow();
        wrap.value().addException(count);
    }

    public void addBlock(int count) {
        CacheWindowWrap<CacheMetricBucket> wrap = data.currentWindow();
        wrap.value().addBlock(count);
    }

    public void addWaiting(long time, int acquireCount) {
        data.addWaiting(time, acquireCount);
    }

    public void addOccupiedPass(int acquireCount) {
        CacheWindowWrap<CacheMetricBucket> wrap = data.currentWindow();
        wrap.value().addOccupiedPass(acquireCount);
    }

    public void addSuccess(int count) {
        CacheWindowWrap<CacheMetricBucket> wrap = data.currentWindow();
        wrap.value().addSuccess(count);
    }

    public void addPass(int count) {
        CacheWindowWrap<CacheMetricBucket> wrap = data.currentWindow();
        wrap.value().addPass(count);
    }

    public void addRT(long rt) {
        CacheWindowWrap<CacheMetricBucket> wrap = data.currentWindow();
        wrap.value().addRT(rt);
    }

    public void debug() {
        data.debug(TimeUtil.currentTimeMillis());
    }

    public long previousWindowBlock() {
        data.currentWindow();
        CacheWindowWrap<CacheMetricBucket> wrap = data.getPreviousWindow();
        if (wrap == null) {
            return 0;
        }
        return wrap.value().block();
    }

    public long previousWindowPass() {
        data.currentWindow();
        CacheWindowWrap<CacheMetricBucket> wrap = data.getPreviousWindow();
        if (wrap == null) {
            return 0;
        }
        return wrap.value().pass();
    }

    public void add(CacheMetricEvent event, long count) {
        data.currentWindow().value().add(event, count);
    }

    public long getCurrentCount(CacheMetricEvent event) {
        return data.currentWindow().value().get(event);
    }

    /**
     * Get total sum for provided event in {@code intervalInSec}.
     *
     * @param event event to calculate
     * @return total sum for event
     */
    public long getSum(CacheMetricEvent event) {
        data.currentWindow();
        long sum = 0;

        List<CacheMetricBucket> buckets = data.values();
        for (CacheMetricBucket bucket : buckets) {
            sum += bucket.get(event);
        }
        return sum;
    }

    /**
     * Get average count for provided event per second.
     *
     * @param event event to calculate
     * @return average count per second for event
     */
    public double getAvg(CacheMetricEvent event) {
        return getSum(event) / data.getIntervalInSecond();
    }

    public long getWindowPass(long timeMillis) {
        CacheMetricBucket bucket = data.getWindowValue(timeMillis);
        if (bucket == null) {
            return 0L;
        }
        return bucket.pass();
    }

    public long waiting() {
        return data.currentWaiting();
    }

    public double getWindowIntervalInSec() {
        return data.getIntervalInSecond();
    }

    public int getSampleCount() {
        return data.getSampleCount();
    }
}