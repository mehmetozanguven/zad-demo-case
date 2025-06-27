package com.mehmetozanguven.zad_demo_case.account.internal.account;

import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialAmount;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.converter.AccountType;
import com.mehmetozanguven.zad_demo_case.core.converter.currency.CurrencyType;
import com.mehmetozanguven.zad_demo_case.core.entity.ApiBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.Objects;

@With
@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
public class Account extends ApiBaseEntity {
    @NotNull
    @Column(name = "account_type")
    private AccountType accountType;

    @NotBlank
    @Column(name = "user_id")
    private String userId;

    @Column(name = "balance")
    private BigDecimal balance;

    @Column(name = "currency_type")
    @NotNull
    private CurrencyType currencyType;

    public void setBalance(FinancialAmount financialAmount) {
        this.balance = financialAmount.givenAmount();
    }

    public FinancialMoney getBalance() {
        if (Objects.isNull(balance)) {
            return new FinancialMoney(FinancialAmount.ZERO, currencyType);
        } else {
            return new FinancialMoney(new FinancialAmount(balance), currencyType);
        }
    }
}
