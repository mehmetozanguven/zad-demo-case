package com.mehmetozanguven.zad_demo_case.account.internal.transaction;

import com.mehmetozanguven.zad_demo_case.account.TransactionModel;
import com.mehmetozanguven.zad_demo_case.account.internal.AccountExchangeOperation;
import com.mehmetozanguven.zad_demo_case.account.internal.account.Account;
import com.mehmetozanguven.zad_demo_case.account.internal.account.AccountRepository;
import com.mehmetozanguven.zad_demo_case.core.ApiApplyOperationResultLogic;
import com.mehmetozanguven.zad_demo_case.core.BusinessUseCase;
import com.mehmetozanguven.zad_demo_case.core.DateOperation;
import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.commonModel.TransactionEvent;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionStatus;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionType;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiErrorInfo;
import lombok.Builder;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Optional;

@BusinessUseCase
@RequiredArgsConstructor
public class CreateNewTransactionUseCase implements  ApiApplyOperationResultLogic<CreateNewTransactionUseCase.CreateTransactionRequest, CreateNewTransactionUseCase.CreateTransactionInnerRequest, TransactionModel> {
    private static final ApiErrorInfo TRANSACTION_FAILED = ApiErrorInfo.TRANSACTION_OPERATION_FAILED;

    @Builder
    public record CreateTransactionRequest(String userId, String accountId,
                                           TransactionType transactionType,
                                           FinancialMoney financialMoney,
                                           TransactionInfo transactionInfo){}
    public record CreateTransactionInnerRequest(Account account, Transaction newTransaction) {};

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;
    private final AccountExchangeOperation accountExchangeOperation;

    @Override
    public OperationResult<CreateTransactionInnerRequest> logicBefore(CreateTransactionRequest request) {
        Optional<Account> accountInDb = accountRepository.findUserAccount(request.userId(), request.accountId());
        if (accountInDb.isEmpty()) {
            return OperationResult.<CreateTransactionInnerRequest>builder()
                    .addException(TRANSACTION_FAILED, "Account not found")
                    .build();
        }
        Account account = accountInDb.get();
        FinancialMoney transactionAmount = request.financialMoney();
        FinancialMoney convertedTransaction = accountExchangeOperation.getTransactionInAccountCurrency(account.getCurrencyType(), transactionAmount);

        Transaction.TransactionBuilder newTransaction = Transaction.builder()
                .transactionType(request.transactionType())
                .transactionStatus(TransactionStatus.WAITING)
                .account(account)
                .currencyType(convertedTransaction.currency())
                .amount(convertedTransaction.amount().givenAmount())
                .expirationTime(DateOperation.addDurationToUTCNow(Duration.ofMinutes(1)))
                .transactionInfo(request.transactionInfo())
                ;

        return OperationResult.<CreateTransactionInnerRequest>builder()
                .addReturnedValue(new CreateTransactionInnerRequest(account, newTransaction.build()))
                .build();
    }

    @Override
    public OperationResult<TransactionModel> executeLogic(CreateTransactionInnerRequest createTransactionInnerRequest) {
        Transaction savedTransaction = transactionRepository.save(createTransactionInnerRequest.newTransaction());
        return OperationResult.<TransactionModel>builder()
                .addReturnedValue(transactionMapper.createModelFromEntity(savedTransaction))
                .build();
    }

    @Override
    public void afterExecution(OperationResult<TransactionModel> response) {
    }
}
