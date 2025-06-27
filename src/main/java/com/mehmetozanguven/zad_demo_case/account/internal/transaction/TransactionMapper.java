package com.mehmetozanguven.zad_demo_case.account.internal.transaction;

import com.mehmetozanguven.zad_demo_case.account.TransactionModel;
import com.mehmetozanguven.zad_demo_case.account.internal.account.AccountMapper;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, uses = AccountMapper.class)
public interface TransactionMapper {

    @Mappings(value = {
            @Mapping(target = "amount", expression = "java(transaction.getAmount())"),
            @Mapping(target = "accountModel", source = "account", qualifiedByName = "createModelFromEntity")
    })
    TransactionModel createModelFromEntity(Transaction transaction);
}
