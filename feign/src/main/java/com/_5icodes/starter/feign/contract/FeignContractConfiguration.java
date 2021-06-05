package com._5icodes.starter.feign.contract;

import feign.Contract;
import org.springframework.context.annotation.Bean;

public class FeignContractConfiguration {
    @Bean
    public Contract defaultContract() {
        return new Contract.Default();
    }
}