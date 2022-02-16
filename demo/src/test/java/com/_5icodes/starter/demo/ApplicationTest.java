package com._5icodes.starter.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = App.class)
@Slf4j
public class ApplicationTest {
    @Autowired
    private TestCustomFeign testCustomFeign;

    @Test
    public void testCustomFeign() {
        testCustomFeign.testCustomFeign(new HashMap<>());
    }
}