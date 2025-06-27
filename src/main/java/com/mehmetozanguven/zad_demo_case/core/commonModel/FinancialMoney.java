package com.mehmetozanguven.zad_demo_case.core.commonModel;

import com.mehmetozanguven.zad_demo_case.core.converter.currency.CurrencyType;
import lombok.Builder;
import lombok.With;

@With
@Builder
public record FinancialMoney(FinancialAmount amount, CurrencyType currency) {
    public FinancialMoney(FinancialAmount amount, CurrencyType currency) {
        this.amount = amount;
        this.currency = currency;
    }
}
