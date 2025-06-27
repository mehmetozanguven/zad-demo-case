package com.mehmetozanguven.zad_demo_case.core.converter.transaction;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TransactionStatusConverter implements AttributeConverter<TransactionStatus, String> {
    @Override
    public String convertToDatabaseColumn(TransactionStatus attribute) {
        return attribute.dbValue;
    }

    @Override
    public TransactionStatus convertToEntityAttribute(String dbData) {
        return TransactionStatus.findByDBValue(dbData);
    }
}
