package com.mehmetozanguven.zad_demo_case.core.converter;

import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialAmount;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.converter.currency.CurrencyType;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionStatus;
import com.mehmetozanguven.zad_swagger_api.contract.openapi.model.AppCurrencyCode;
import com.mehmetozanguven.zad_swagger_api.contract.openapi.model.AppMoney;
import com.mehmetozanguven.zad_swagger_api.contract.openapi.model.AppTransactionStatus;

public class SwaggerAppConverter {

    public static AppTransactionStatus fromTransactionStatus(TransactionStatus transactionStatus) {
        return AppTransactionStatus.fromValue(transactionStatus.getValue());
    }

    public static AppMoney fromApiMoney(FinancialMoney financialMoney) {
        AppCurrencyCode appCurrencyCode = AppCurrencyCode.fromValue(financialMoney.currency().getValue());
        return new AppMoney()
                .money(financialMoney.amount().givenAmount())
                .currencyType(appCurrencyCode);
    }

    public static FinancialMoney fromAppMoney(AppMoney appMoney) {
        CurrencyType currencyType = SwaggerAppConverter.fromAppCurrencyCode(appMoney.getCurrencyType());
        return new FinancialMoney(new FinancialAmount(appMoney.getMoney()), currencyType);
    }

    public static CurrencyType fromAppCurrencyCode(AppCurrencyCode appCurrencyCode) {
        return CurrencyType.findByClientValue(appCurrencyCode.getValue());
    }
}
