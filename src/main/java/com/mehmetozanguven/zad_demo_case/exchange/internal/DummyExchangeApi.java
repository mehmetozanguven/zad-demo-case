package com.mehmetozanguven.zad_demo_case.exchange.internal;

import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialAmount;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.converter.currency.CurrencyType;
import com.mehmetozanguven.zad_demo_case.core.converter.exchange.ExchangeType;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiErrorInfo;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@RequiredArgsConstructor
public class DummyExchangeApi implements ExchangeApi {

    @Override
    public OperationResult<ExchangeApiResult> getCurrency(ExchangeType exchangeType) {
        if (ExchangeType.USD_TRY.equals(exchangeType)) {
            return OperationResult.<ExchangeApiResult>builder()
                    .addReturnedValue(ExchangeApiResult.builder()
                            .exchangeType(exchangeType)
                            .financialMoney(new FinancialMoney(new FinancialAmount(BigDecimal.valueOf(39.66)), CurrencyType.TRY))
                            .build())
                    .build();
        } else if (ExchangeType.TRY_USD.equals(exchangeType)) {
            return OperationResult.<ExchangeApiResult>builder()
                    .addReturnedValue(ExchangeApiResult.builder()
                            .exchangeType(exchangeType)
                            .financialMoney(new FinancialMoney(new FinancialAmount(BigDecimal.valueOf(0.025)), CurrencyType.USD))
                            .build())
                    .build();
        } else {
            return OperationResult.<ExchangeApiResult>builder()
                    .addException(ApiErrorInfo.EXCHANGE_OPERATION_FAILED)
                    .build();
        }
    }
}
