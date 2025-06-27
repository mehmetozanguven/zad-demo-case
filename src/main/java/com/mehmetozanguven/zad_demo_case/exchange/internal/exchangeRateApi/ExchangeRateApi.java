package com.mehmetozanguven.zad_demo_case.exchange.internal.exchangeRateApi;

import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialAmount;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.converter.currency.CurrencyType;
import com.mehmetozanguven.zad_demo_case.core.converter.exchange.ExchangeType;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiErrorInfo;
import com.mehmetozanguven.zad_demo_case.exchange.internal.ExchangeApi;
import com.mehmetozanguven.zad_demo_case.exchange.internal.ExchangeApiResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class ExchangeRateApi implements ExchangeApi {
    private record ExchangeRateResponse(
            String result,
            String documentation,
            String time_last_update_utc,
            Map<String, Double> conversion_rates
    ) {}

    private static final String API_URL = "https://v6.exchangerate-api.com/v6";
    private final RestTemplate exchangeRateApiRestTemplate;
    private final String apiToken;

    @Autowired
    public ExchangeRateApi(RestTemplate exchangeRateApiRestTemplate) {
        this.exchangeRateApiRestTemplate = exchangeRateApiRestTemplate;
        this.apiToken = "DUMMY_TOKEN_CAN_BE_LOADED_FROM_APP.YML";
    }

    public ExchangeRateResponse getLatestRates(ExchangeType exchangeType) {
        try {
            String url = API_URL + "/" + apiToken +  "/latest/" + exchangeType.fromCurrency.getValue();
            return exchangeRateApiRestTemplate.getForObject(url, ExchangeRateResponse.class);
        } catch (Exception ex) {
            log.error("Api exception", ex);
            return null;
        }
    }

    @Override
    public OperationResult<ExchangeApiResult> getCurrency(ExchangeType exchangeType) {
        ExchangeRateResponse exchangeRateResponse = getLatestRates(exchangeType);
        if (Objects.isNull(exchangeRateResponse) || Objects.isNull(exchangeRateResponse.conversion_rates())) {
            return OperationResult.<ExchangeApiResult>builder()
                    .addException(ApiErrorInfo.EXCHANGE_OPERATION_FAILED)
                    .build();
        }
        if (ExchangeType.USD_TRY.equals(exchangeType)) {
            Double rate = exchangeRateResponse.conversion_rates().get(exchangeType.toCurrency.getValue());
            return OperationResult.<ExchangeApiResult>builder()
                    .addReturnedValue(ExchangeApiResult.builder()
                            .exchangeType(exchangeType)
                            .financialMoney(new FinancialMoney(new FinancialAmount(BigDecimal.valueOf(rate)), CurrencyType.TRY))
                            .build())
                    .build();
        } else if (ExchangeType.TRY_USD.equals(exchangeType)) {
            Double rate = exchangeRateResponse.conversion_rates().get(exchangeType.toCurrency.getValue());
            return OperationResult.<ExchangeApiResult>builder()
                    .addReturnedValue(ExchangeApiResult.builder()
                            .exchangeType(exchangeType)
                            .financialMoney(new FinancialMoney(new FinancialAmount(BigDecimal.valueOf(rate)), CurrencyType.USD))
                            .build())
                    .build();
        } else {
            return OperationResult.<ExchangeApiResult>builder()
                    .addException(ApiErrorInfo.EXCHANGE_OPERATION_FAILED)
                    .build();
        }
    }
}
