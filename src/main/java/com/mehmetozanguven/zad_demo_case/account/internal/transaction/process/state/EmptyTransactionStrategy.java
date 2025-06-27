package com.mehmetozanguven.zad_demo_case.account.internal.transaction.process.state;

import com.mehmetozanguven.zad_demo_case.account.internal.transaction.Transaction;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.process.ProcessTransactionResponse;
import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class EmptyTransactionStrategy implements TransactionStateStrategy {

    @Override
    public OperationResult<ProcessTransactionResponse> doAction(Transaction transaction) {
        log.info("There is no action for the given transactionStatus: {}, Transaction: {}", transaction.getTransactionStatus(), transaction);
        return OperationResult.<ProcessTransactionResponse>builder()
                .addReturnedValue(new ProcessTransactionResponse())
                .build();
    }
}
