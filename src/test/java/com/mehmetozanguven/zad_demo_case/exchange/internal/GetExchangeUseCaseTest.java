package com.mehmetozanguven.zad_demo_case.exchange.internal;

import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialAmount;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.converter.exchange.ExchangeType;
import com.mehmetozanguven.zad_demo_case.exchange.ExchangeModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetExchangeUseCaseTest {
    @Mock
    ExchangeApi exchangeApi;
    @Mock
    ExchangeRepository exchangeRepository;
    ExchangeMapper exchangeMapper = new ExchangeMapperImpl();

    GetExchangeUseCase getExchangeUseCase;

    @BeforeEach
    void beforeEach() {
        getExchangeUseCase = new GetExchangeUseCase(exchangeApi, exchangeRepository, exchangeMapper);
    }

    @Test
    void applyBusiness_ShouldReturnValidExchange_IfExists() {
        when(exchangeRepository.findExchangeByType(any(), any(), any())).thenReturn(
                new SliceImpl<>(List.of(Exchange.builder().exchangeType(ExchangeType.TRY_USD).exchangeRate(FinancialAmount.ZERO.givenAmount()).id("test").build()), PageRequest.of(0,1), false)
        );
        var request = new GetExchangeUseCase.GetExchangeRequest(ExchangeType.TRY_USD);
        OperationResult<ExchangeModel> result = getExchangeUseCase.applyBusiness(request);

        verify(exchangeApi, times(0)).getCurrency(any());
        Assertions.assertAll(
                () -> Assertions.assertTrue(result.isValid()),
                () -> Assertions.assertEquals("test", result.getReturnedValue().getId())
        );
    }
}