package com.mehmetozanguven.zad_demo_case.core.converter;

public enum UserStatus {
    UNKNOWN("UNKNOWN", "UNKNOWN"),
    WAITING_FOR_APPROVAL_NOT_ACTIVE("WAITING_FOR_APPROVAL_NOT_ACTIVE", "WAITING_FOR_APPROVAL_NOT_ACTIVE"),
    APPROVED_AND_ACTIVE("APPROVED_AND_ACTIVE", "APPROVED_AND_ACTIVE")
    ;

    public final String dbValue;
    public final String value;

    UserStatus(String dbValue, String value) {
        this.dbValue = dbValue;
        this.value = value;
    }

    public static UserStatus fromDbValue(String dbValue) {
        for (UserStatus each : values()) {
            if (each.dbValue.equals(dbValue)) {
                return each;
            }
        }
        return UserStatus.UNKNOWN;
    }
}
