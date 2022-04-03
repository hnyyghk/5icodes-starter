package com._5icodes.starter.jdbc.monitor;

import com._5icodes.starter.common.utils.JsonUtils;
import com._5icodes.starter.common.utils.RegionUtils;
import com._5icodes.starter.common.utils.SpringApplicationUtils;
import com._5icodes.starter.jdbc.SqlMonitorRecord;
import com.alibaba.druid.stat.DruidStatService;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Iterator;
import java.util.List;

public class DruidMetricCollector {
    private final static String QUERY_SQL = "/sql.json";
    private final static String RESET_ALL = "/reset-all.json";
    private final static String DATASOURCE_INFO = "/datasource.json";

    private final DruidStatService druidStatService;

    public DruidMetricCollector() {
        this(DruidStatService.getInstance());
    }

    public DruidMetricCollector(DruidStatService druidStatService) {
        this.druidStatService = druidStatService;
    }

    @Data
    private static class DruidStatDTO {
        private Integer ResultCode;
        private List<SqlMonitorRecord> Content;
    }

    /**
     * 获取SQL执行情况并重新开始统计
     *
     * @return
     */
    public List<SqlMonitorRecord> collect() {
        //获取SQL执行情况
        String result = druidStatService.service(QUERY_SQL);
        //重新开始统计
        druidStatService.service(RESET_ALL);
        if (!StringUtils.hasText(result)) {
            return null;
        }
        DruidStatDTO druidStatDTO = JsonUtils.parse(result, DruidStatDTO.class);
        if (druidStatDTO.getResultCode() != 1) {
            return null;
        }
        List<SqlMonitorRecord> sqlMonitorRecordList = druidStatDTO.getContent();
        if (CollectionUtils.isEmpty(sqlMonitorRecordList)) {
            return null;
        }
        long time = System.currentTimeMillis();
        String zone = RegionUtils.getZone();
        Iterator<SqlMonitorRecord> iterator = sqlMonitorRecordList.iterator();
        while (iterator.hasNext()) {
            SqlMonitorRecord sqlMonitorRecord = iterator.next();
            if (sqlMonitorRecord == null) {
                iterator.remove();
                continue;
            }
            sqlMonitorRecord.setTime(time);
            sqlMonitorRecord.setZone(zone);
            sqlMonitorRecord.setMonitorType(MonitorType.SQL);
            sqlMonitorRecord.setApp(SpringApplicationUtils.getApplicationName());
        }
        return sqlMonitorRecordList;
    }

    public List<SqlMonitorRecord> collectConnectInfo() {
        String result = druidStatService.service(DATASOURCE_INFO);
        if (!StringUtils.hasText(result)) {
            return null;
        }
        DruidStatDTO druidStatDTO = JsonUtils.parse(result, DruidStatDTO.class);
        if (druidStatDTO.getResultCode() != 1) {
            return null;
        }
        List<SqlMonitorRecord> sqlMonitorRecordList = druidStatDTO.getContent();
        if (CollectionUtils.isEmpty(sqlMonitorRecordList)) {
            return null;
        }
        long time = System.currentTimeMillis();
        String zone = RegionUtils.getZone();
        Iterator<SqlMonitorRecord> iterator = sqlMonitorRecordList.iterator();
        while (iterator.hasNext()) {
            SqlMonitorRecord sqlMonitorRecord = iterator.next();
            if (sqlMonitorRecord == null) {
                iterator.remove();
                continue;
            }
            sqlMonitorRecord.setTime(time);
            sqlMonitorRecord.setZone(zone);
            sqlMonitorRecord.setMonitorType(MonitorType.CONNECT);
            sqlMonitorRecord.setApp(SpringApplicationUtils.getApplicationName());
            if (StringUtils.hasText(sqlMonitorRecord.getURL())) {
                sqlMonitorRecord.setURL(sqlMonitorRecord.getURL().substring(0, sqlMonitorRecord.getURL().indexOf("?")));
            }
        }
        return sqlMonitorRecordList;
    }
}