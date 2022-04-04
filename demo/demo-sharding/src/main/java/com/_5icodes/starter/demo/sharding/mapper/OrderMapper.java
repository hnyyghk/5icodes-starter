package com._5icodes.starter.demo.sharding.mapper;

import com._5icodes.starter.demo.sharding.entity.Order;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface OrderMapper extends Mapper<Order> {
    List<Order> queryAllOrders();
}