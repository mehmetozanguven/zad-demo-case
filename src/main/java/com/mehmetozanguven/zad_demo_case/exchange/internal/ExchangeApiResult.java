package com.mehmetozanguven.zad_demo_case.exchange.internal;

import com.mehmetozanguven.zad_demo_case.core.converter.exchange.ExchangeType;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import lombok.Builder;

@Builder
public record ExchangeApiResult(
        ExchangeType exchangeType,
        FinancialMoney financialMoney
) {
}
