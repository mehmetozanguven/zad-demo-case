package com.mehmetozanguven.zad_demo_case.account.internal.transaction.process.state;

import com.mehmetozanguven.zad_demo_case.account.internal.transaction.Transaction;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.TransactionInfo;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.process.ProcessTransactionResponse;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.process.type.TransactionTypeStrategyRegistry;
import com.mehmetozanguven.zad_demo_case.core.DateOperation;
import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@TransactionStatusHandler(TransactionStatus.WAITING)
public class WaitingTransactionStrategy implements TransactionStateStrategy {
    private final TransactionTypeStrategyRegistry typeStrategyRegistry;

    @Override
    public OperationResult<ProcessTransactionResponse> doAction(Transaction transaction) {
        boolean isExpired = DateOperation.getOffsetNowAsUTC().isAfter(transaction.getExpirationTime());
        if (isExpired) {
            transaction.setTransactionStatus(TransactionStatus.FAIL);
            transaction.setTransactionInfo(TransactionInfo.builder().code("-99").message("Can not process transaction within expiration time").build());
            var response = new ProcessTransactionResponse(List.of(transaction), List.of());
            return OperationResult.<ProcessTransactionResponse>builder()
                    .addReturnedValue(response)
                    .build();
        } else {
            return typeStrategyRegistry.getHandler(transaction.getTransactionType()).doAction(transaction);
        }
    }
}
