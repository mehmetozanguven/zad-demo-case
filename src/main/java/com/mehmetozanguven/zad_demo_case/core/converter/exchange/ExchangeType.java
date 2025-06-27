package com.mehmetozanguven.zad_demo_case.core.converter.exchange;

import com.mehmetozanguven.zad_demo_case.core.converter.currency.CurrencyType;
import org.apache.commons.lang3.StringUtils;

public enum ExchangeType {
    UNKNOWN(CurrencyType.UNKNOWN, CurrencyType.UNKNOWN),
    USD_TRY(CurrencyType.USD, CurrencyType.TRY),
    TRY_USD(CurrencyType.TRY, CurrencyType.USD)
    ;

    public final CurrencyType fromCurrency;
    public final CurrencyType toCurrency;
    public final String value;
    public final String dbValue;

    ExchangeType(CurrencyType fromCurrency, CurrencyType toCurrency) {
        this.fromCurrency = fromCurrency;
        this.toCurrency = toCurrency;
        this.value = fromCurrency.getValue() + "-" + toCurrency.getValue();
        this.dbValue = fromCurrency.getDbValue() + "!!" + toCurrency.getDbValue();
    }

    public static ExchangeType findByDBValue(String value) {
        for (ExchangeType each : values()) {
            if (StringUtils.equals(each.dbValue, value)) {
                return each;
            }
        }
        return ExchangeType.UNKNOWN;
    }

    public static ExchangeType createExchangeType(CurrencyType from, CurrencyType to) {
         for (ExchangeType each : values()) {
             if (each.fromCurrency.equals(from) && each.toCurrency.equals(to)) {
                 return each;
             }
         }
         return ExchangeType.UNKNOWN;
    }
}
