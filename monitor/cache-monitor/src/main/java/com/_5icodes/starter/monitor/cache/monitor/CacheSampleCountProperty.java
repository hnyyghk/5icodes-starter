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
 * Holds statistic buckets count per second.
 *
 * @author jialiang.linjl
 * @author CarpenterLee
 */
public class CacheSampleCountProperty {
    /**
     * <p>
     * Statistic buckets count per second. This variable determines sensitivity of the QPS calculation.
     * </p>
     * Node that this value must be divisor of 1000.
     */
    public static volatile int SAMPLE_COUNT = 2;
}