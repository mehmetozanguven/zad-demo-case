package com.mehmetozanguven.zad_demo_case.account.internal.account;

import com.mehmetozanguven.zad_demo_case.account.AccountModel;
import com.mehmetozanguven.zad_demo_case.core.ApiApplyOperationResultLogic;
import com.mehmetozanguven.zad_demo_case.core.BusinessUseCase;
import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Set;

@BusinessUseCase
@RequiredArgsConstructor
public class SearchAccountByUserIDUseCase implements ApiApplyOperationResultLogic<AccountModel, AccountModel, List<AccountModel>> {

    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;

    @Override
    public OperationResult<AccountModel> logicBefore(AccountModel accountModel) {
        return OperationResult.<AccountModel>builder()
                .addReturnedValue(accountModel)
                .build();
    }

    @Override
    public OperationResult<List<AccountModel>> executeLogic(AccountModel accountModel) {
        Set<Account> userAccounts = accountRepository.getUserAccounts(accountModel.getUserId());
        return OperationResult.<List<AccountModel>>builder()
                .addReturnedValue(accountMapper.createModelsFromEntities(userAccounts))
                .build();
    }

    @Override
    public void afterExecution(OperationResult<List<AccountModel>> response) {

    }
}
