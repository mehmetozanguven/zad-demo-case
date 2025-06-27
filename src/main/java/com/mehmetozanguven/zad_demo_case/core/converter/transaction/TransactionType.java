package com.mehmetozanguven.zad_demo_case.core.converter.transaction;

import lombok.Getter;

@Getter
public enum TransactionType {
    UNKNOWN( "UNKNOWN", "UNKNOWN"),
    DEPOSIT( "DEPOSIT", "DEPOSIT"),
    WITHDRAW("WITHDRAW", "WITHDRAW"),
    EXCHANGE("EXCHANGE", "EXCHANGE")
    ;
    public final String dbValue;
    public final String value;

    TransactionType(String dbValue, String value) {
        this.dbValue = dbValue;
        this.value = value;
    }

    public static TransactionType findByDBValue(String givenValue) {
        for (TransactionType each : TransactionType.values()) {
            if (givenValue.equals(each.dbValue)) {
                return each;
            }
        }
        return TransactionType.UNKNOWN;
    }
}
