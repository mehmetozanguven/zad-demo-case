package com.mehmetozanguven.zad_demo_case.core.converter.appBoolean;

import lombok.Getter;

@Getter
public enum AppBooleanValue {
    TRUE("true"),
    FALSE("false"),
    NEITHER_TRUE_NOR_FALSE("neither_true_nor_false")
    ;
    public final String value;

    AppBooleanValue(String value) {
        this.value = value;
    }

    public boolean isTrue() {
        return this == AppBooleanValue.TRUE;
    }

    public static AppBooleanValue createBooleanValue(String value) {
        for (AppBooleanValue each : AppBooleanValue.values()) {
            if (each.value.equals(value)) {
                return each;
            }
        }
        return AppBooleanValue.NEITHER_TRUE_NOR_FALSE;
    }
}
