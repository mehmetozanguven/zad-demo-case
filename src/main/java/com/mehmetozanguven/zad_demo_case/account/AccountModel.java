package com.mehmetozanguven.zad_demo_case.account;

import com.mehmetozanguven.zad_demo_case.core.commonModel.ApiBaseModel;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.converter.AccountType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class AccountModel extends ApiBaseModel {
    private AccountType accountType;
    private String userId;
    private FinancialMoney balance;

}
