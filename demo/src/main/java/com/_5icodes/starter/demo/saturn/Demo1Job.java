package com._5icodes.starter.demo.saturn;

import com.vip.saturn.job.AbstractSaturnJavaJob;
import com.vip.saturn.job.SaturnJobExecutionContext;
import com.vip.saturn.job.SaturnJobReturn;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Demo1Job extends AbstractSaturnJavaJob {
    @Autowired
    private DemoService demoService;

    @Override
    public SaturnJobReturn handleJavaJob(String jobName, Integer shardItem, String shardParam,
                                         SaturnJobExecutionContext shardingContext) throws InterruptedException {
        log.info("{} is running, item is {}", jobName, shardItem);
        demoService.doing();
        return new SaturnJobReturn();
    }
}