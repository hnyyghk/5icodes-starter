package com._5icodes.starter.demo.jdbc.mapper;

import com._5icodes.starter.demo.jdbc.entity.Order;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface OrderMapper extends Mapper<Order> {
    List<Order> queryAllOrders();
}