package com.mehmetozanguven.zad_demo_case.account;

import com.mehmetozanguven.zad_demo_case.core.commonModel.ApiBaseModel;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionStatus;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.OffsetDateTime;

@Getter
@Setter
@SuperBuilder
public class TransactionModel extends ApiBaseModel {
    private AccountModel accountModel;
    private TransactionType transactionType;
    private FinancialMoney amount;
    private TransactionStatus transactionStatus;
    private OffsetDateTime processedAt;
    private OffsetDateTime expirationTime;
}
