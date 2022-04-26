package com._5icodes.starter.demo.drools;

import lombok.Data;

@Data
public class Sale {
    private int quantity;
    private String item;
    private int discount;
}