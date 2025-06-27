package com.mehmetozanguven.zad_demo_case.core.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AccountTypeConverter implements AttributeConverter<AccountType, String> {
    @Override
    public String convertToDatabaseColumn(AccountType attribute) {
        return attribute.dbValue;
    }

    @Override
    public AccountType convertToEntityAttribute(String dbData) {
        return AccountType.fromDbValue(dbData);
    }
}
