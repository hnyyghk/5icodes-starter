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
 * The fundamental data structure for metric statistics in a time span.
 *
 * @author jialiang.linjl
 * @author Eric Zhao
 * @see CacheLeapArray
 */
public class CacheBucketLeapArray extends CacheLeapArray<CacheMetricBucket> {

    public CacheBucketLeapArray(int sampleCount, int intervalInMs) {
        super(sampleCount, intervalInMs);
    }

    @Override
    public CacheMetricBucket newEmptyBucket(long time) {
        return new CacheMetricBucket();
    }

    @Override
    protected CacheWindowWrap<CacheMetricBucket> resetWindowTo(CacheWindowWrap<CacheMetricBucket> w, long startTime) {
        // Update the start time and reset value.
        w.resetTo(startTime);
        w.value().reset();
        return w;
    }
}