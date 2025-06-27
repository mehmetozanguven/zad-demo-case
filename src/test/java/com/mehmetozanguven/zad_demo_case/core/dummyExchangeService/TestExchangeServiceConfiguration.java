package com.mehmetozanguven.zad_demo_case.core.dummyExchangeService;

import com.mehmetozanguven.zad_demo_case.exchange.ExchangeService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestExchangeServiceConfiguration {

    @Bean
    public ExchangeService exchangeService() {
        return new TestExchangeService();
    }
}
