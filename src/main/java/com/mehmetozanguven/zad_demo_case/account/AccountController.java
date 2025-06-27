package com.mehmetozanguven.zad_demo_case.account;

import com.mehmetozanguven.zad_demo_case.core.converter.AccountType;

import com.mehmetozanguven.zad_demo_case.core.converter.SwaggerAppConverter;
import com.mehmetozanguven.zad_swagger_api.contract.openapi.api.controller.AccountControllerApi;
import com.mehmetozanguven.zad_swagger_api.contract.openapi.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AccountController implements AccountControllerApi {
    private final AccountService accountService;

    @Override
    public AccountApiResponse doCreateAccountForUser(CreateAccountRequest createAccountRequest) {
        AccountModel accountRequest= AccountModel.builder()
                .accountType(AccountType.fromClientValue(createAccountRequest.getAccountType().getValue()))
                .userId(createAccountRequest.getUserId().getValue())
                .build();
        AccountModel createdAccount = accountService.addAccountIntoUser(accountRequest);

        AccountResponse accountResponse = new AccountResponse()
                .addAccountIdsItem(createdAccount.getId())
                .totalBalance(SwaggerAppConverter.fromApiMoney(createdAccount.getBalance()))
                ;
        return new AccountApiResponse()
                .isSuccess(true)
                .httpStatusCode(HttpStatus.OK.value())
                .response(accountResponse)
                ;
    }

    @Override
    public AccountApiResponse doGetUserAccountsWithSpecifiedCurrency(String userId, AppCurrencyCode currencyType) {
        CurrentBalance currentBalance = accountService.retrieveCurrentBalanceInSpecifiedCurrency(AccountModel.builder().userId(userId).build(), SwaggerAppConverter.fromAppCurrencyCode(currencyType));

        AccountResponse accountResponse = new AccountResponse()
                .accountIds(currentBalance.accountIds())
                .totalBalance(SwaggerAppConverter.fromApiMoney(currentBalance.totalBalance()));
        return new AccountApiResponse()
                .isSuccess(true)
                .httpStatusCode(HttpStatus.OK.value())
                .response(accountResponse)
                ;
    }
}
