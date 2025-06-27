package com.mehmetozanguven.zad_demo_case.exchange.internal;

import com.mehmetozanguven.zad_demo_case.core.BaseDataJPATestcontainerTest;
import com.mehmetozanguven.zad_demo_case.core.DateOperation;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialAmount;
import com.mehmetozanguven.zad_demo_case.core.converter.exchange.ExchangeType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.Optional;
import java.util.stream.Stream;

class ExchangeRepositoryTest extends BaseDataJPATestcontainerTest {
    @Autowired
    TestEntityManager testEntityManager;
    @Autowired
    ExchangeRepository exchangeRepository;


    @ParameterizedTest
    @MethodSource(value = "newExchanges")
    void test_findExchangeByType(Exchange exchange, boolean isExists) {
        exchange = exchangeRepository.save(exchange);
        Assertions.assertNotNull(exchange.getId());

        Slice<Exchange> byExchangeType = exchangeRepository.findExchangeByType(exchange.getExchangeType(), DateOperation.getOffsetNowAsUTC(), PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createTimestampMilli")));
        Assertions.assertAll(
                () -> Assertions.assertEquals(isExists, byExchangeType.hasContent())
        );
    }

    static Stream<Arguments> newExchanges() {
        return Stream.of(
                Arguments.of(
                        Exchange.builder()
                                .exchangeType(ExchangeType.TRY_USD)
                                .exchangeRate(FinancialAmount
                                        .builder()
                                        .givenAmount(BigDecimal.valueOf(10.234))
                                        .build()
                                        .givenAmount()
                                )
                                .expirationTime(DateOperation.addDurationToUTCNow(Duration.ofMinutes(1)))
                                .build(),
                        true
                ),
                Arguments.of(
                        Exchange.builder()
                                .exchangeType(ExchangeType.USD_TRY)
                                .exchangeRate(FinancialAmount
                                        .builder()
                                        .givenAmount(BigDecimal.valueOf(9))
                                        .build()
                                        .givenAmount()
                                )
                                .expirationTime(DateOperation.minusDuration(DateOperation.getOffsetNowAsUTC(), Duration.ofMinutes(1)))
                                .build(),
                        false
                )
        );
    }
}