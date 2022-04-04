package com._5icodes.starter.demo.sharding;

import com._5icodes.starter.sharding.annotation.EachSource;
import com._5icodes.starter.sharding.annotation.EnableShardingSource;
import com._5icodes.starter.sharding.constants.SourceTypeEnum;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableShardingSource(prefix = "sharding", sources = {
        @EachSource(dbPrefix = "test",
                sourceType = SourceTypeEnum.MASTER_ONLY,
                basePackages = "com._5icodes.starter.demo.sharding.mapper",
                mapperLocations = "classpath*:mappers/*.xml")
})
public class ShardingDemoApplication {
    public static void main(final String[] args) {
        SpringApplication.run(ShardingDemoApplication.class, args);
    }
}