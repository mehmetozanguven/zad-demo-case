package com.mehmetozanguven.zad_demo_case.account.internal.transaction.process;

import com.mehmetozanguven.zad_demo_case.account.internal.account.Account;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.Transaction;

import java.util.List;

public record ProcessTransactionResponse(
        List<Transaction> updatedTransactions,
        List<Account> updatedAccounts
) {
    public ProcessTransactionResponse(List<Transaction> updatedTransactions, List<Account> updatedAccounts) {
        this.updatedTransactions = updatedTransactions;
        this.updatedAccounts = updatedAccounts;
    }

    public ProcessTransactionResponse() {
        this(List.of(), List.of());
    }
}
