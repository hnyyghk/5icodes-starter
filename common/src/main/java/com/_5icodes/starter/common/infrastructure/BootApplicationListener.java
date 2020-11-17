package com._5icodes.starter.common.infrastructure;

import com._5icodes.starter.common.utils.SpringApplicationUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.SpringApplicationEvent;
import org.springframework.context.ApplicationListener;

public interface BootApplicationListener<E extends SpringApplicationEvent> extends ApplicationListener<E> {
    @Override
    default void onApplicationEvent(E event) {
        SpringApplication springApplication = event.getSpringApplication();
        if (!SpringApplicationUtils.isBootApplication(springApplication)) {
            return;
        }
        doOnApplicationEvent(event);
    }

    void doOnApplicationEvent(E event);
}