package com.mehmetozanguven.zad_demo_case.account.internal.account;

import com.mehmetozanguven.zad_demo_case.account.AccountModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    AccountModel clone(AccountModel in);

    @Mappings(value = {
            @Mapping(target = "currencyType", ignore = true),
            @Mapping(target = "balance", ignore = true)
    })
    Account createEntityFromModel(AccountModel accountModel);

    @Named("createModelFromEntity")
    @Mappings(value = {
            @Mapping(target = "balance", expression = "java(account.getBalance())")
    })
    AccountModel createModelFromEntity(Account account);

    List<AccountModel> createModelsFromEntities(Collection<Account> accounts);
}
