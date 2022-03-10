package com._5icodes.starter.common.advisor;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.lang.annotation.Annotation;

@Data
@AllArgsConstructor
public class BeanAnnotation<T extends Annotation> {
    private String beanName;
    private T annotation;
}