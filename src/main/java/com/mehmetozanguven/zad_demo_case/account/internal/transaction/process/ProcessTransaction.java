package com.mehmetozanguven.zad_demo_case.account.internal.transaction.process;

import com.mehmetozanguven.zad_demo_case.account.TransactionModel;
import com.mehmetozanguven.zad_demo_case.account.internal.account.AccountRepository;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.Transaction;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.TransactionMapper;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.TransactionRepository;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.process.state.TransactionStateStrategy;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.process.state.TransactionStrategyRegistry;
import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.commonModel.TransactionEvent;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiErrorInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessTransaction {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionStrategyRegistry registry;
    private final TransactionMapper transactionMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW, timeout = 5000)
    public OperationResult<TransactionModel> processTransaction(TransactionEvent transactionEvent) {
        Optional<Transaction> inDB = transactionRepository.findByPessimisticLockBeforeExpiration(transactionEvent.transactionId());
        if (inDB.isEmpty()) {
            log.error("There is no transaction for the given ID: {}", transactionEvent.transactionId());
            return OperationResult.<TransactionModel>builder()
                    .addException(ApiErrorInfo.TRANSACTION_NOT_FOUND)
                    .build();
        }

        try {
            Transaction transaction = inDB.get();
            TransactionStateStrategy foundStatusStrategy = registry.getHandler(transaction.getTransactionStatus());
            OperationResult<ProcessTransactionResponse> response = foundStatusStrategy.doAction(transaction);
            response.validateResult();
            ProcessTransactionResponse processTransactionResponse = response.getReturnedValue();

            if (!processTransactionResponse.updatedTransactions().isEmpty()) {
                transactionRepository.saveAll(processTransactionResponse.updatedTransactions());
            }

            if (!processTransactionResponse.updatedAccounts().isEmpty()) {
                accountRepository.saveAll(processTransactionResponse.updatedAccounts());
            }

            return OperationResult.<TransactionModel>builder()
                    .addReturnedValue(transactionMapper.createModelFromEntity(inDB.get()))
                    .build();
        } catch (Exception ex) {
            log.error("processTransaction exception. Rollback", ex);
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return OperationResult.<TransactionModel>builder()
                    .addException(ApiErrorInfo.TRANSACTION_OPERATION_FAILED)
                    .build();
        }
    }
}
