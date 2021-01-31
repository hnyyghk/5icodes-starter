/*
 * Copyright 1999-2019 Alibaba Group Holding Ltd.
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
 * @author jialiang.linjl
 * @author Carpenter Lee
 * @since 1.5.0
 */
public class CacheOccupyTimeoutProperty {
    /**
     * <p>
     * Max occupy timeout in milliseconds. Requests with priority can occupy tokens of the future statistic
     * window, and {@code occupyTimeout} limit the max time length that can be occupied.
     * </p>
     * <p>
     * Note that the timeout value should never be greeter than {@link CacheIntervalProperty#INTERVAL}.
     * </p>
     */
    public static volatile int INTERVAL = 500;
}