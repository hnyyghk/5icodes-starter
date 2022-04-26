package com._5icodes.starter.demo.drools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DiscountController {
    @Autowired
    private DiscountService discountService;

    @PostMapping("/discount")
    private Sale getDiscountPercent(@RequestBody Sale sale) {
        discountService.applyDiscount(sale);
        return sale;
    }
}