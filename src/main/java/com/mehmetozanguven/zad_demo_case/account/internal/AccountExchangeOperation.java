package com.mehmetozanguven.zad_demo_case.account.internal;

import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.converter.currency.CurrencyType;
import com.mehmetozanguven.zad_demo_case.core.converter.exchange.ExchangeType;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiErrorInfo;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiException;
import com.mehmetozanguven.zad_demo_case.exchange.ExchangeResult;
import com.mehmetozanguven.zad_demo_case.exchange.ExchangeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Component
@RequiredArgsConstructor
public class AccountExchangeOperation {
    private final ExchangeService exchangeService;

    public FinancialMoney getTransactionInAccountCurrency(CurrencyType accountCurrencyType, FinancialMoney financialMoney) {
        if (accountCurrencyType.equals(financialMoney.currency())) {
            return financialMoney;
        }

        try {
            ExchangeType exchangeType = ExchangeType.createExchangeType(financialMoney.currency(), accountCurrencyType);
            ExchangeResult result = CompletableFuture
                    .supplyAsync(() -> exchangeService.getExchange(exchangeType, financialMoney))
                    .get(300, TimeUnit.MILLISECONDS);
            return result.financialMoney();
        } catch (TimeoutException e) {
            log.error("Can not get exchange within specified timeframe. Failing the request", e);
            throw new ApiException(ApiErrorInfo.EXCHANGE_OPERATION_FAILED);
        } catch (Exception e) {
            log.error("Unexpected error in exchange operation", e);
            throw new ApiException(ApiErrorInfo.EXCHANGE_OPERATION_FAILED);
        }
    }
}
