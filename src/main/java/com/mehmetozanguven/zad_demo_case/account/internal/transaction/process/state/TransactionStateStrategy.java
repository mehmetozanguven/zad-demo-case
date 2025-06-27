package com.mehmetozanguven.zad_demo_case.account.internal.transaction.process.state;

import com.mehmetozanguven.zad_demo_case.account.internal.transaction.Transaction;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.process.ProcessTransactionResponse;
import com.mehmetozanguven.zad_demo_case.core.OperationResult;

public interface TransactionStateStrategy {

    OperationResult<ProcessTransactionResponse> doAction(Transaction transaction);
}
