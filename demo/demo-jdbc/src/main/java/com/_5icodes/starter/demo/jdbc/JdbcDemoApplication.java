package com._5icodes.starter.demo.jdbc;

import com._5icodes.starter.demo.jdbc.entity.Order;
import com._5icodes.starter.demo.jdbc.repository.OrderRepository;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import tk.mybatis.spring.annotation.MapperScan;

@MapperScan(basePackages = "com._5icodes.starter.demo.jdbc.mapper")
@SpringBootApplication
public class JdbcDemoApplication {
    public static void main(final String[] args) {
        SpringApplication.run(JdbcDemoApplication.class, args);
    }

    @Bean
    InitializingBean saveData(OrderRepository repo) {
        return () -> {
            for (long i = 0; i < 1001; i++) {
                String str = String.valueOf(i);
                repo.save(new Order(i, str + str, str + str + str));
            }
        };
    }
}