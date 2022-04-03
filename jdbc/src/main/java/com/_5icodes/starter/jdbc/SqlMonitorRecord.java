package com._5icodes.starter.jdbc;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SqlMonitorRecord {
    //应用名称
    private String App;
    //执行SQL语句
    private String SQL;
    //最大并发数量
    private Integer ConcurrentMax;
    //数据库类型
    private String DbType;
    //更新行数(这里是插入、更新、删除操作)
    private Integer EffectedRowCount;
    //最大更新行数(这里是插入、更新、删除操作)
    private Integer EffectedRowCountMax;
    //错误数量
    private Integer ErrorCount;
    //执行次数
    private Integer ExecuteCount;
    //读取行数
    private Integer FetchRowCount;
    //最大读取行数
    private Integer FetchRowCountMax;
    private Integer InTransactionCount;
    //最大耗时
    private Integer MaxTimespan;
    //总共耗时
    private Integer TotalTime;
    private Integer BatchSizeMax;
    private Integer BatchSizeTotal;
    private Integer BlobOpenCount;
    private Integer ClobOpenCount;
    private Integer ActiveCount;
    private Integer PoolingCount;
    private String URL;
    //当前上报的时间戳
    private Long time;
    private String DataSource;
    private String monitorType;
    private String zone;
}