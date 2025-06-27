package com.mehmetozanguven.zad_demo_case.exchange;

import com.mehmetozanguven.zad_demo_case.core.commonModel.ApiBaseModel;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialAmount;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.converter.exchange.ExchangeType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Getter
@Setter
@SuperBuilder
public class ExchangeModel extends ApiBaseModel {
    private ExchangeType exchangeType;
    private FinancialAmount exchangeRate;
    private OffsetDateTime expirationTime;

    public FinancialMoney getAsFinanceMoney() {
        return new FinancialMoney(exchangeRate, exchangeType.toCurrency);
    }
}
