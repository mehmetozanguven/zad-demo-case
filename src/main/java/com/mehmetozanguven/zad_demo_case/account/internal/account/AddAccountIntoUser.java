package com.mehmetozanguven.zad_demo_case.account.internal.account;

import com.mehmetozanguven.zad_demo_case.account.AccountModel;
import com.mehmetozanguven.zad_demo_case.core.ApiApplyOperationResultLogic;
import com.mehmetozanguven.zad_demo_case.core.BusinessUseCase;
import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialAmount;
import com.mehmetozanguven.zad_demo_case.core.converter.AccountType;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiErrorInfo;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiExceptionInfo;
import lombok.RequiredArgsConstructor;

import java.util.Objects;

@BusinessUseCase
@RequiredArgsConstructor
public class AddAccountIntoUser implements ApiApplyOperationResultLogic<AccountModel, AccountModel, AccountModel> {
    private final AccountRepository accountRepository;
    private final AccountMapper accountMapper;
    private static final int MAX_ALLOWED_ACCOUNT = 2;
    private static final ApiExceptionInfo ACCOUNT_OPERATION_FAILED = ApiErrorInfo.ACCOUNT_OPERATION_FAILED;

    @Override
    public OperationResult<AccountModel> logicBefore(AccountModel accountModel) {
        if (Objects.isNull(accountModel.getAccountType()) || Objects.isNull(accountModel.getUserId())) {
            return OperationResult.<AccountModel>builder()
                    .addException(ApiErrorInfo.INVALID_REQUEST)
                    .build();
        }

        if (AccountType.UNKNOWN.equals(accountModel.getAccountType())) {
            return OperationResult.<AccountModel>builder()
                    .addException(ApiErrorInfo.INVALID_REQUEST)
                    .build();
        }

        boolean hasAccount = accountRepository.hasUserAccountForType(accountModel.getUserId(), accountModel.getAccountType());
        if (hasAccount) {
            return OperationResult.<AccountModel>builder()
                    .addException(ACCOUNT_OPERATION_FAILED, "There is an already account for " + accountModel.getAccountType().value)
                    .build();
        }

        long totalDistinctAccount = accountRepository.totalDistinctAccountForUser(accountModel.getUserId());
        if (totalDistinctAccount >= MAX_ALLOWED_ACCOUNT) {
            return OperationResult.<AccountModel>builder()
                    .addException(ACCOUNT_OPERATION_FAILED, "Can not create more than " + MAX_ALLOWED_ACCOUNT + " accounts")
                    .build();
        }

        return OperationResult.<AccountModel>builder()
                .addReturnedValue(accountModel)
                .build();
    }

    @Override
    public OperationResult<AccountModel> executeLogic(AccountModel accountModel) {
        Account account = accountMapper.createEntityFromModel(accountModel);
        account.setCurrencyType(accountModel.getAccountType().getCurrencyTypeForAccountType());
        account.setBalance(FinancialAmount.ZERO);
        account = accountRepository.save(account);

        return OperationResult.<AccountModel>builder()
                .addReturnedValue(accountMapper.createModelFromEntity(account))
                .build();
    }

    @Override
    public void afterExecution(OperationResult<AccountModel> response) {

    }
}
