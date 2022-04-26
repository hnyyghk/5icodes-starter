package com._5icodes.starter.demo;

import com._5icodes.starter.demo.drools.DiscountService;
import com._5icodes.starter.demo.drools.Sale;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BootDroolsEmbeddedApplicationTests {
    @Autowired
    private DiscountService discountService;

    @Test
    void discountOnCarWithQuantityMoreThan5ShouldBe10() {
        Sale sale = new Sale();
        sale.setItem("Car");
        sale.setQuantity(11);
        discountService.applyDiscount(sale);
        assertEquals(10, sale.getDiscount());
    }

    @Test
    void discountOnBikeWithQuantityMoreThan10ShouldBe15() {
        Sale sale = new Sale();
        sale.setItem("Bike");
        sale.setQuantity(11);
        discountService.applyDiscount(sale);
        assertEquals(15, sale.getDiscount());
    }
}