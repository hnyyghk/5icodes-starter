package com._5icodes.starter.demo.jdbc.controller;

import com._5icodes.starter.demo.jdbc.entity.Order;
import com._5icodes.starter.demo.jdbc.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class JdbcDemoController {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/jdbc")
    public Object getData() {
        return jdbcTemplate.queryForList("SELECT * FROM t_order LIMIT 10");
    }

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/hello")
    public List<Order> hello() {
        return orderRepository.findAll();
    }

    @GetMapping("/log")
    public void getLog() {
        try {
            int i = 2 / 0;
        } catch (ArithmeticException e) {
            throw new RuntimeException("test", e);
        }
    }
}