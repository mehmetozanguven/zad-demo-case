package com.mehmetozanguven.zad_demo_case.core.converter.appBoolean;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AppBooleanValueJPAConverter implements AttributeConverter<AppBooleanValue, String> {
    @Override
    public String convertToDatabaseColumn(AppBooleanValue attribute) {
        return attribute.value;
    }

    @Override
    public AppBooleanValue convertToEntityAttribute(String dbData) {
        return AppBooleanValue.createBooleanValue(dbData);
    }
}
