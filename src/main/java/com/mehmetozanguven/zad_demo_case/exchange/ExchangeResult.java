package com.mehmetozanguven.zad_demo_case.exchange;

import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import lombok.Builder;

@Builder
public record ExchangeResult(
        String exchangeId,
        FinancialMoney financialMoney
) {
}
