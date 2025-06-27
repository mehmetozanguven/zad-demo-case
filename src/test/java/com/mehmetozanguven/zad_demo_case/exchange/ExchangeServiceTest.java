package com.mehmetozanguven.zad_demo_case.exchange;

import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialAmount;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.converter.currency.CurrencyType;
import com.mehmetozanguven.zad_demo_case.core.converter.exchange.ExchangeType;
import com.mehmetozanguven.zad_demo_case.exchange.internal.GetExchangeUseCase;
import com.mehmetozanguven.zad_demo_case.exchange.internal.ValidateGivenExchangeRateUseCase;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExchangeServiceTest {
    @Mock
    ValidateGivenExchangeRateUseCase validateGivenExchangeRateUseCase;;
    @Mock
    GetExchangeUseCase getExchangeUseCase;

    ExchangeService exchangeService;

    @BeforeEach
    void beforeEach() {
        exchangeService = new ExchangeService(validateGivenExchangeRateUseCase, getExchangeUseCase);
    }

    @ParameterizedTest
    @MethodSource(value = "exchangeParams")
    void getExchange_ShouldReturnCorrectValue(ExchangeModel dummyExchangeResult, ExchangeType exchangeType, FinancialMoney givenMoney, FinancialMoney resultedMoney) {
        Mockito.reset(getExchangeUseCase);

        when(getExchangeUseCase.applyBusiness(any())).thenReturn(OperationResult.<ExchangeModel>builder()
                .addReturnedValue(dummyExchangeResult)
                .build());
        ExchangeResult exchangeResult = exchangeService.getExchange(exchangeType, FinancialMoney.builder()
                .amount(givenMoney.amount())
                .currency(givenMoney.currency())
                .build());

        Assertions.assertAll(
                () -> Assertions.assertEquals("test", exchangeResult.exchangeId()),
                () -> Assertions.assertEquals(resultedMoney.amount().givenAmount(), exchangeResult.financialMoney().amount().givenAmount()),
                () -> Assertions.assertEquals(resultedMoney.currency(), exchangeResult.financialMoney().currency())
        );
    }

    static Stream<Arguments> exchangeParams() {
        FinancialAmount usd_try = new FinancialAmount(BigDecimal.valueOf(50.000));
        FinancialAmount try_usd = new FinancialAmount(BigDecimal.valueOf(0.25));

        return Stream.of(
                Arguments.of(
                        // convert 2-USD > TRY
                        ExchangeModel.builder()
                                .id("test")
                                .exchangeType(ExchangeType.USD_TRY)
                                .exchangeRate(usd_try)
                                .build(),
                        ExchangeType.USD_TRY,
                        new FinancialMoney(new FinancialAmount(BigDecimal.valueOf(2)),  CurrencyType.USD),
                        new FinancialMoney(new FinancialAmount(usd_try.givenAmount().multiply(BigDecimal.valueOf(2))),  CurrencyType.TRY)
                ),
                Arguments.of(
                        // convert 2-USD > USD
                        ExchangeModel.builder()
                                .id("test")
                                .exchangeType(ExchangeType.TRY_USD)
                                .exchangeRate(usd_try)
                                .build(),
                        ExchangeType.TRY_USD,
                        new FinancialMoney(new FinancialAmount(BigDecimal.valueOf(2)),  CurrencyType.USD),
                        new FinancialMoney(new FinancialAmount(BigDecimal.valueOf(2)),  CurrencyType.USD)
                ),
                Arguments.of(
                        // convert 2-TRY > USD
                        ExchangeModel.builder()
                                .id("test")
                                .exchangeType(ExchangeType.TRY_USD)
                                .exchangeRate(try_usd)
                                .build(),
                        ExchangeType.TRY_USD,
                        new FinancialMoney(new FinancialAmount(BigDecimal.valueOf(2)),  CurrencyType.TRY),
                        new FinancialMoney(new FinancialAmount(try_usd.givenAmount().multiply(BigDecimal.valueOf(2))),  CurrencyType.USD)
                )
        );
    }
}