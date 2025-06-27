package com.mehmetozanguven.zad_demo_case.account;

import com.mehmetozanguven.zad_demo_case.account.internal.account.AddAccountIntoUser;
import com.mehmetozanguven.zad_demo_case.account.internal.account.GetAccountBalanceInGivenCurrencyUseCase;
import com.mehmetozanguven.zad_demo_case.account.internal.account.SearchAccountByUserIDUseCase;
import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.converter.currency.CurrencyType;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiErrorInfo;
import com.mehmetozanguven.zad_demo_case.core.pipeline.ApiPipeline;
import com.mehmetozanguven.zad_demo_case.user.UserModel;
import com.mehmetozanguven.zad_demo_case.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final UserService userService;
    private final AddAccountIntoUser addAccountIntoUser;
    private final SearchAccountByUserIDUseCase searchAccountByUserIDUseCase;
    private final GetAccountBalanceInGivenCurrencyUseCase getAccountBalanceInGivenCurrencyUseCase;

    public CurrentBalance retrieveCurrentBalanceInSpecifiedCurrency(AccountModel searchByUserId, CurrencyType currencyType) {
        ApiPipeline<AccountModel, CurrentBalance> pipeline = ApiPipeline
                .<AccountModel>start()
                .pipe(input -> {
                    List<AccountModel> userAccounts = retrieveAccountsByUserId(input);
                    return OperationResult.<GetAccountBalanceInGivenCurrencyUseCase.AccountBalanceInGivenCurrencyRequest>builder()
                            .addReturnedValue(new GetAccountBalanceInGivenCurrencyUseCase.AccountBalanceInGivenCurrencyRequest(userAccounts, currencyType))
                            .build();
                }).pipe(getAccountBalanceInGivenCurrencyUseCase::applyBusiness);
        return pipeline.execute(searchByUserId);
    }

    public List<AccountModel> retrieveAccountsByUserId(AccountModel accountModel) {
        OperationResult<List<AccountModel>> result = searchAccountByUserIDUseCase.applyBusiness(accountModel);
        if (result.isValid()) {
            return result.getReturnedValue();
        } else {
            return List.of();
        }
    }

    public AccountModel addAccountIntoUser(AccountModel newAccount) {
        ApiPipeline<AccountModel, AccountModel> pipeline = ApiPipeline
                .<AccountModel>start()
                .pipe(input -> {
                    Optional<UserModel> isExists = userService.isUserExistsById(UserModel.builder().id(input.getUserId()).build());
                    if (isExists.isPresent()) {
                        input.setUserId(isExists.get().getId());
                        return OperationResult.<AccountModel>builder()
                                .addReturnedValue(input)
                                .build();
                    } else {
                        return OperationResult.<AccountModel>builder()
                                .addException(ApiErrorInfo.INVALID_REQUEST)
                                .build();
                    }
                })
                .pipe(addAccountIntoUser::applyBusiness);

        return pipeline.execute(newAccount);
    }
}
