package com.mehmetozanguven.zad_demo_case.core.converter.exchange;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ExchangeTypeConverter implements AttributeConverter<ExchangeType, String> {
    @Override
    public String convertToDatabaseColumn(ExchangeType attribute) {
        return attribute.dbValue;
    }

    @Override
    public ExchangeType convertToEntityAttribute(String dbData) {
        return ExchangeType.findByDBValue(dbData);
    }
}
