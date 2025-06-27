package com.mehmetozanguven.zad_demo_case.exchange.internal;

import com.mehmetozanguven.zad_demo_case.exchange.ExchangeModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface ExchangeMapper {

    ExchangeModel fromEntity(Exchange exchange);

    @Mappings(value = {
            @Mapping(target = "exchangeRate", expression = "java(exchangeModel.getExchangeRate().givenAmount())")
    })
    Exchange fromModel(ExchangeModel exchangeModel);
}
