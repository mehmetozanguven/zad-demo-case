package com.mehmetozanguven.zad_demo_case.account.internal.transaction.process.state;


import com.mehmetozanguven.zad_demo_case.account.internal.transaction.Transaction;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.process.ProcessTransactionResponse;
import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@TransactionStatusHandler(TransactionStatus.FAIL)
public class FailedTransactionStrategy implements TransactionStateStrategy {

    @Override
    public OperationResult<ProcessTransactionResponse> doAction(Transaction transaction) {
        log.error("Failed transaction should not processed again, transaction: {}", transaction);
        return OperationResult.<ProcessTransactionResponse>builder()
                .addReturnedValue(new ProcessTransactionResponse())
                .build();
    }
}
