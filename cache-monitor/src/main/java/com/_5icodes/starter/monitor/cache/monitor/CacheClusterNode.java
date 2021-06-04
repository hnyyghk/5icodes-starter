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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

/**
 * <p>
 * This class stores summary runtime statistics of the resource, including rt, thread count, qps
 * and so on. Same resource shares the same {@link CacheClusterNode} globally.
 * </p>
 * <p>
 * To distinguish invocation from different origin,
 * one {@link CacheClusterNode} holds an {@link #originCountMap}, this map holds {@link CacheStatisticNode}
 * of different origin. Use {@link #getOrCreateOriginNode(String)} to get {@link CacheStatisticNode} of the specific
 * origin.<br/>
 * Note that 'origin' usually is Service Consumer's app name.
 * </p>
 *
 * @author qinan.qn
 * @author jialiang.linjl
 */
public class CacheClusterNode extends CacheStatisticNode {
    /**
     * <p>The origin map holds the pair: (origin, originNode) for one specific resource.</p>
     * <p>
     * The longer the application runs, the more stable this mapping will become.
     * So we didn't use concurrent map here, but a lock, as this lock only happens
     * at the very beginning while concurrent map will hold the lock all the time.
     * </p>
     */
    private static Map<String, CacheStatisticNode> originCountMap = new HashMap<>();

    private static final ReentrantLock lock = new ReentrantLock();

    /**
     * <p>Get {@link CacheStatisticNode} of the specific origin. Usually the origin is the Service Consumer's app name.</p>
     * <p>If the origin node for given origin is absent, then a new {@link CacheStatisticNode}
     * for the origin will be created and returned.</p>
     *
     * @param origin The caller's name.
     * @return the {@link CacheStatisticNode} of the specific origin
     */
    public static CacheStatisticNode getOrCreateOriginNode(String origin) {
        CacheStatisticNode statisticNode = originCountMap.get(origin);
        if (statisticNode == null) {
            lock.lock();
            try {
                statisticNode = originCountMap.get(origin);
                if (statisticNode == null) {
                    // The node is absent, create a new node for the origin.
                    statisticNode = new CacheStatisticNode();
                    HashMap<String, CacheStatisticNode> newMap = new HashMap<>(originCountMap.size() + 1);
                    newMap.putAll(originCountMap);
                    newMap.put(origin, statisticNode);
                    originCountMap = newMap;
                }
            } finally {
                lock.unlock();
            }
        }
        return statisticNode;
    }

    public static Map<String, CacheStatisticNode> getOriginCountMap() {
        return originCountMap;
    }
}