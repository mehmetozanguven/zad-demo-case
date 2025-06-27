package com.mehmetozanguven.zad_demo_case.core.converter;

import com.mehmetozanguven.zad_demo_case.core.converter.currency.CurrencyType;

public enum AccountType {
    UNKNOWN("UNKNOWN", "UNKNOWN"),
    TRY("TRY", "TRY"),
    USD("USD", "USD")
    ;

    public final String dbValue;
    public final String value;

    AccountType(String dbValue, String value) {
        this.dbValue = dbValue;
        this.value = value;
    }

    public static AccountType fromClientValue(String dbValue) {
        for (AccountType each : values()) {
            if (each.value.equals(dbValue)) {
                return each;
            }
        }
        return AccountType.UNKNOWN;
    }

    public static AccountType fromDbValue(String dbValue) {
        for (AccountType each : values()) {
            if (each.dbValue.equals(dbValue)) {
                return each;
            }
        }
        return AccountType.UNKNOWN;
    }

    public CurrencyType getCurrencyTypeForAccountType() {
        if (AccountType.TRY.equals(this)) {
            return CurrencyType.TRY;
        } else if (AccountType.USD.equals(this)) {
            return CurrencyType.USD;
        } else {
            return CurrencyType.UNKNOWN;
        }
    }
}
