package com.mehmetozanguven.zad_demo_case.core.converter.transaction;

import lombok.Getter;

@Getter
public enum TransactionStatus {
    UNKNOWN( "UNKNOWN", "UNKNOWN"),
    WAITING( "WAITING", "WAITING"),
    SUCCESS("SUCCESS", "SUCCESS"),
    FAIL("FAIL", "FAIL")
    ;
    public final String dbValue;
    public final String value;

    TransactionStatus(String dbValue, String value) {
        this.dbValue = dbValue;
        this.value = value;
    }

    public static TransactionStatus findByDBValue(String givenValue) {
        for (TransactionStatus each : TransactionStatus.values()) {
            if (givenValue.equals(each.dbValue)) {
                return each;
            }
        }
        return TransactionStatus.UNKNOWN;
    }
}
