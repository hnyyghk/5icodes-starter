package com._5icodes.starter.webmvc.monitor;

import com._5icodes.starter.common.utils.GrayUtils;
import com._5icodes.starter.common.utils.IpUtils;
import com._5icodes.starter.common.utils.RegionUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com._5icodes.starter.web.log.StaticAccessLogCollector;

import java.util.HashMap;
import java.util.Map;

/**
 * 被调日志上报
 */
public class WebMvcCalleeAccessLogCollector implements StaticAccessLogCollector {
    @Override
    public void collect(Map<String, Object> accessLog) {
        Map<String, Object> callee = new HashMap<>();
        callee.put("app", SpringApplicationUtils.getApplicationName());
        callee.put("groupId", GrayUtils.isGray() ? "GRAY" : "PRD");
        callee.put("ip", IpUtils.getHostAddress());
        callee.put("zone", RegionUtils.getZone());
        accessLog.put("callee", callee);
    }
}