package com.mehmetozanguven.zad_demo_case.core.commonModel;

import lombok.Builder;
import lombok.With;

import java.math.BigDecimal;
import java.math.RoundingMode;

@With
@Builder
public record FinancialAmount(BigDecimal givenAmount) {
    public FinancialAmount(BigDecimal givenAmount) {
        this.givenAmount = givenAmount.setScale(2, RoundingMode.HALF_UP); //monetary values always have exactly 2 decimal places, which is standard in most financial systems.
    }

    public static FinancialAmount ZERO = new FinancialAmount(BigDecimal.ZERO);

    public static boolean isLessThanGivenAmount(FinancialAmount financialAmount, FinancialAmount givenAmount) {
        return financialAmount.givenAmount().compareTo(givenAmount.givenAmount()) < 0;
    }
}
