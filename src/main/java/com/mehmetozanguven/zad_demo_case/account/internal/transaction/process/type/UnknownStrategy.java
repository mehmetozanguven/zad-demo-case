package com.mehmetozanguven.zad_demo_case.account.internal.transaction.process.type;

import com.mehmetozanguven.zad_demo_case.account.internal.transaction.Transaction;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.TransactionInfo;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.process.ProcessTransactionResponse;
import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionStatus;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@TransactionTypeHandler(TransactionType.UNKNOWN)
public class UnknownStrategy implements TransactionTypeStrategy {

    @Override
    public OperationResult<ProcessTransactionResponse> doAction(Transaction transaction) {
        log.error("Unknown strategy, make transaction status failed");
        transaction.setTransactionStatus(TransactionStatus.FAIL);
        transaction.setTransactionInfo(TransactionInfo.builder().code("-1").message("Unknown transactionType").build());
        return OperationResult.<ProcessTransactionResponse>builder()
                .addReturnedValue(new ProcessTransactionResponse(List.of(transaction), List.of()))
                .build();
    }
}
