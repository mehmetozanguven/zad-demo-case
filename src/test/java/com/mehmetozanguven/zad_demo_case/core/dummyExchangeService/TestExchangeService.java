package com.mehmetozanguven.zad_demo_case.core.dummyExchangeService;

import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialAmount;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.converter.currency.CurrencyType;
import com.mehmetozanguven.zad_demo_case.core.converter.exchange.ExchangeType;
import com.mehmetozanguven.zad_demo_case.exchange.ExchangeResult;
import com.mehmetozanguven.zad_demo_case.exchange.ExchangeService;
import com.mehmetozanguven.zad_demo_case.exchange.internal.GetExchangeUseCase;
import com.mehmetozanguven.zad_demo_case.exchange.internal.ValidateGivenExchangeRateUseCase;
import org.mockito.Mockito;

import java.math.BigDecimal;

public class TestExchangeService extends ExchangeService {
    private final static FinancialAmount TRY_USD = new FinancialAmount(BigDecimal.valueOf(0.5));
    private final static FinancialAmount USD_TRY = new FinancialAmount(BigDecimal.valueOf(50));

    public TestExchangeService() {
        super(Mockito.mock(ValidateGivenExchangeRateUseCase.class), Mockito.mock(GetExchangeUseCase.class));
    }

    @Override
    public ExchangeResult getExchange(ExchangeType exchangeType, FinancialMoney financialMoney) {
        if (ExchangeType.TRY_USD.equals(exchangeType)) {
            BigDecimal resultedDecimal = financialMoney.amount().givenAmount().multiply(TRY_USD.givenAmount());
            FinancialAmount resultedAmount = new FinancialAmount(resultedDecimal);
            FinancialMoney result = new FinancialMoney(resultedAmount, CurrencyType.USD);

            return ExchangeResult.builder()
                    .exchangeId("test-try-usd")
                    .financialMoney(result)
                    .build();
        } else if (ExchangeType.USD_TRY.equals(exchangeType)) {
            BigDecimal resultedDecimal = financialMoney.amount().givenAmount().multiply(USD_TRY.givenAmount());
            FinancialAmount resultedAmount = new FinancialAmount(resultedDecimal);
            FinancialMoney result = new FinancialMoney(resultedAmount, CurrencyType.TRY);

            return ExchangeResult.builder()
                    .exchangeId("test-usd-try")
                    .financialMoney(result)
                    .build();
        } else {
            throw new UnsupportedOperationException();
        }
    }
}
