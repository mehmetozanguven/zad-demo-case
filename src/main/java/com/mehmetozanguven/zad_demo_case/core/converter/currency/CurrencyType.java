package com.mehmetozanguven.zad_demo_case.core.converter.currency;

import lombok.Getter;

@Getter
public enum CurrencyType  {
    UNKNOWN( "UNKNOWN", "UNKNOWN"),
    TRY( "TRY", "TRY"),
    USD("USD", "USD")
    ;
    public final String dbValue;
    public final String value;

    CurrencyType(String dbValue, String value) {
        this.dbValue = dbValue;
        this.value = value;
    }

    public static CurrencyType findByClientValue(String givenValue) {
        for (CurrencyType each : CurrencyType.values()) {
            if (givenValue.equals(each.value)) {
                return each;
            }
        }
        return CurrencyType.UNKNOWN;
    }

    public static CurrencyType findByDBValue(String givenValue) {
        for (CurrencyType each : CurrencyType.values()) {
            if (givenValue.equals(each.dbValue)) {
                return each;
            }
        }
        return CurrencyType.UNKNOWN;
    }
}
