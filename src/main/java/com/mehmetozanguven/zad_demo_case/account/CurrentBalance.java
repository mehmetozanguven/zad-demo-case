package com.mehmetozanguven.zad_demo_case.account;

import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import lombok.Builder;

import java.util.List;

@Builder
public record CurrentBalance(
        List<String> accountIds,
        FinancialMoney totalBalance
) {
}
