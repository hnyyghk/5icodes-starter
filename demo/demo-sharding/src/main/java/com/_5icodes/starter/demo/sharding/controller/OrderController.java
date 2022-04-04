package com._5icodes.starter.demo.sharding.controller;

import com._5icodes.starter.demo.sharding.entity.Order;
import com._5icodes.starter.demo.sharding.mapper.OrderMapper;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderMapper orderMapper;

    @GetMapping("/page")
    public Object getPageData(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                              @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        return PageHelper.startPage(pageNum, pageSize).setOrderBy("Id ASC").doSelectPageInfo(() -> orderMapper.selectAll());
    }

    @GetMapping("/insert")
    public Object insert() {
        Random random = new Random();
        long l = random.nextLong();
        Order order = Order.builder().id(l).orderId(String.valueOf(l)).detail("this is test for order").build();
        orderMapper.insert(order);
        return order;
    }

    @GetMapping("/selectAll")
    public Object selectAll() {
        return orderMapper.selectAll();
    }

    @GetMapping("/selectByPrimaryKey")
    public Object selectByPrimaryKey(Long id) {
        return orderMapper.selectByPrimaryKey(id);
    }

    @GetMapping("/queryAllOrders")
    public Object queryAllOrders() {
        return orderMapper.queryAllOrders();
    }

    @GetMapping("/select")
    public Object select() {
        Order order = Order.builder().orderId("11").build();
        return orderMapper.select(order);
    }
}