package com.mehmetozanguven.zad_demo_case.exchange;

import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialAmount;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.converter.exchange.ExchangeType;
import com.mehmetozanguven.zad_demo_case.core.pipeline.ApiPipeline;
import com.mehmetozanguven.zad_demo_case.exchange.internal.GetExchangeUseCase;
import com.mehmetozanguven.zad_demo_case.exchange.internal.ValidateGivenExchangeRateUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ExchangeService {

    private final ValidateGivenExchangeRateUseCase validateGivenExchangeRateUseCase;
    private final GetExchangeUseCase getExchangeUseCase;

    public OperationResult<ExchangeModel> validateGivenExchange(ExchangeModel exchangeModel) {
        return validateGivenExchangeRateUseCase.applyBusiness(exchangeModel);
    }

    public ExchangeResult getExchange(ExchangeType exchangeType, FinancialMoney financialMoney) {
        var exchangeRequest = new GetExchangeUseCase.GetExchangeRequest(exchangeType);
        ApiPipeline<GetExchangeUseCase.GetExchangeRequest, ExchangeResult> pipeline = ApiPipeline
                .<GetExchangeUseCase.GetExchangeRequest>start()
                .pipe(getExchangeUseCase::applyBusiness)
                .pipe(exchangeModel -> {
                    if (exchangeModel.getExchangeType().fromCurrency.equals(financialMoney.currency())) {
                        BigDecimal resultedDecimal = financialMoney.amount().givenAmount().multiply(exchangeModel.getExchangeRate().givenAmount());
                        FinancialAmount resultedAmount = new FinancialAmount(resultedDecimal);
                        return OperationResult.<ExchangeResult>builder()
                                .addReturnedValue(ExchangeResult.builder()
                                        .exchangeId(exchangeModel.getId())
                                        .financialMoney(new FinancialMoney(resultedAmount, exchangeModel.getExchangeType().toCurrency))
                                        .build())
                                .build();
                    }
                    return OperationResult.<ExchangeResult>builder()
                            .addReturnedValue(ExchangeResult.builder()
                                    .exchangeId(exchangeModel.getId())
                                    .financialMoney(financialMoney)
                                    .build())
                            .build();
                });

        return pipeline.execute(exchangeRequest);
    }
}
