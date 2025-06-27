package com.mehmetozanguven.zad_demo_case.core.converter.currency;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class CurrencyTypeConverter implements AttributeConverter<CurrencyType, String> {
    @Override
    public String convertToDatabaseColumn(CurrencyType attribute) {
        return attribute.dbValue;
    }

    @Override
    public CurrencyType convertToEntityAttribute(String dbData) {
        return CurrencyType.findByDBValue(dbData);
    }
}
