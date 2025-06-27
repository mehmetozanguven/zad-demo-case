package com.mehmetozanguven.zad_demo_case.account;

import com.mehmetozanguven.zad_demo_case.account.internal.transaction.CreateNewTransactionUseCase;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.Transaction;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.TransactionInfo;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.TransactionRepository;
import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.converter.SwaggerAppConverter;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionStatus;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionType;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiErrorInfo;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiException;
import com.mehmetozanguven.zad_swagger_api.contract.openapi.model.DepositRequest;
import com.mehmetozanguven.zad_swagger_api.contract.openapi.model.ExchangeRequest;
import com.mehmetozanguven.zad_swagger_api.contract.openapi.model.TransactionResponse;
import com.mehmetozanguven.zad_swagger_api.contract.openapi.model.WithdrawRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {
    private final CreateNewTransactionUseCase createNewTransactionUseCase;
    private final TransactionRepository transactionRepository;

    public TransactionResponse getTransactionById(String transactionId) {
        Optional<Transaction> inDB = transactionRepository.findById(transactionId);
        if (inDB.isEmpty()) {
            throw new ApiException(ApiErrorInfo.TRANSACTION_NOT_FOUND);
        }
        Transaction transaction = inDB.get();
        return new TransactionResponse()
                .transactionId(transaction.getId())
                .transactionStatus(SwaggerAppConverter.fromTransactionStatus(transaction.getTransactionStatus()))
                .accountId(transaction.getAccount().getId())
                .amount(SwaggerAppConverter.fromApiMoney(transaction.getAmount()))
        ;
    }

    public TransactionResponse createDepositTransaction(DepositRequest depositRequest) {
        FinancialMoney financialMoney = SwaggerAppConverter.fromAppMoney(depositRequest.getMoney());
        var request = CreateNewTransactionUseCase.CreateTransactionRequest.builder()
                .transactionType(TransactionType.DEPOSIT)
                .userId(depositRequest.getUserId().getValue())
                .accountId(depositRequest.getAccountId().getValue())
                .financialMoney(financialMoney)
                .build();
        return createTransaction(request);
    }

    public TransactionResponse createWithdrawTransaction(WithdrawRequest withdrawRequest) {
        FinancialMoney financialMoney = SwaggerAppConverter.fromAppMoney(withdrawRequest.getMoney());
        var request = CreateNewTransactionUseCase.CreateTransactionRequest.builder()
                .transactionType(TransactionType.WITHDRAW)
                .userId(withdrawRequest.getUserId().getValue())
                .accountId(withdrawRequest.getAccountId().getValue())
                .financialMoney(financialMoney)
                .build();
        return createTransaction(request);
    }

    public TransactionResponse createExchangeRequest(ExchangeRequest exchangeRequest) {
        FinancialMoney financialMoney = SwaggerAppConverter.fromAppMoney(exchangeRequest.getMoney());
        var request = CreateNewTransactionUseCase.CreateTransactionRequest.builder()
                .transactionType(TransactionType.EXCHANGE)
                .userId(exchangeRequest.getFromUserId().getValue())
                .accountId(exchangeRequest.getFromAccountId().getValue())
                .financialMoney(financialMoney)
                .transactionInfo(TransactionInfo.builder()
                        .targetAccountId(exchangeRequest.getToAccountId().getValue())
                        .build())
                .build();
        return createTransaction(request);
    }

    private TransactionResponse createTransaction(CreateNewTransactionUseCase.CreateTransactionRequest request) {
        OperationResult<TransactionModel> createdTransaction = createNewTransactionUseCase.applyBusiness(request);
        if (createdTransaction.isValid()) {
            TransactionModel created = createdTransaction.getReturnedValue();
            return new TransactionResponse()
                    .transactionId(created.getId())
                    .transactionStatus(SwaggerAppConverter.fromTransactionStatus(created.getTransactionStatus()))
                    .accountId(created.getAccountModel().getId())
                    .amount(SwaggerAppConverter.fromApiMoney(created.getAmount()))
                    ;
        } else {
            return new TransactionResponse()
                    .transactionId(StringUtils.EMPTY)
                    .transactionStatus(SwaggerAppConverter.fromTransactionStatus(TransactionStatus.FAIL))
                    .accountId(StringUtils.EMPTY)
                    ;
        }
    }
}
