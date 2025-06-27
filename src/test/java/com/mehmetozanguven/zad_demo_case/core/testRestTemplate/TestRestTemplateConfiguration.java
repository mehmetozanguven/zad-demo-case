package com.mehmetozanguven.zad_demo_case.core.testRestTemplate;

import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;

public class TestRestTemplateConfiguration {


    @Bean
    public TestRestTemplate testRestTemplate() {
        return new TestRestTemplate();
    }
}
