package com.mehmetozanguven.zad_demo_case.core.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserStatusJPAConverter implements AttributeConverter<UserStatus, String> {
    @Override
    public String convertToDatabaseColumn(UserStatus attribute) {
        return attribute.dbValue;
    }

    @Override
    public UserStatus convertToEntityAttribute(String dbData) {
        return UserStatus.fromDbValue(dbData);
    }
}
