package com.mehmetozanguven.zad_demo_case.exchange.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
public class ExchangeApiConfiguration {
    private final RestTemplate exchangeRateApiRestTemplate;

    @Bean
    public ExchangeApi exchangeApi() {
        return new DummyExchangeApi();
    }
}
