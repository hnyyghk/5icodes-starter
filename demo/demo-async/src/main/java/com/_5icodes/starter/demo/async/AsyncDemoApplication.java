package com._5icodes.starter.demo.async;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class AsyncDemoApplication {
    public static void main(final String[] args) {
        SpringApplication.run(AsyncDemoApplication.class, args);
    }

    @Autowired
    private HelloService helloService;

    @GetMapping("/hello")
    public String hello(String name) {
        helloService.sayHello(name);
        return "hello";
    }

    @GetMapping("/hi")
    public String hi(String name) {
        helloService.sayHi(name);
        return "hi";
    }

    @GetMapping("/protect")
    public String protect(String name) {
        helloService.protect(name);
        return "protect";
    }

    @GetMapping("/failRetry")
    public String failRetry(String name) {
        helloService.failRetry(name);
        return "failRetry";
    }

    @Autowired
    private UserService userService;

    @GetMapping("/user/hello")
    public String helloUser() throws Exception {
        User user = new User();
        user.setId(RandomUtils.nextInt());
        user.setName(RandomStringUtils.randomAlphanumeric(8));
        userService.sayHelloOrderly(user, RandomStringUtils.randomAlphanumeric(10));
        return "hello";
    }
}