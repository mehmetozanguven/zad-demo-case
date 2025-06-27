package com.mehmetozanguven.zad_demo_case.account.internal.transaction.process.type;

import com.mehmetozanguven.zad_demo_case.account.internal.account.Account;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.Transaction;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.TransactionInfo;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.process.ProcessTransactionResponse;
import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialAmount;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionStatus;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionType;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiErrorInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@TransactionTypeHandler(TransactionType.DEPOSIT)
public class DepositStrategy implements TransactionTypeStrategy {
    @Override
    public OperationResult<ProcessTransactionResponse> doAction(Transaction transaction) {
        List<Transaction> updatedTransactions = new ArrayList<>();
        List<Account> updatedAccounts = new ArrayList<>();
        try {
            Account transactionAccount = transaction.getAccount();
            FinancialMoney currentMoney = transactionAccount.getBalance();

            FinancialAmount transactionAmount = transaction.getAmount().amount();

            FinancialAmount currentAmount = currentMoney.amount();
            FinancialAmount nextAmount = new FinancialAmount(currentAmount.givenAmount().add(transactionAmount.givenAmount()));
            transactionAccount.setBalance(nextAmount);
            transaction.setTransactionStatus(TransactionStatus.SUCCESS);

            updatedTransactions.add(transaction);
            updatedAccounts.add(transactionAccount);

            return OperationResult.<ProcessTransactionResponse>builder()
                    .addReturnedValue(new ProcessTransactionResponse(updatedTransactions, updatedAccounts))
                    .build();

        } catch (Exception ex) {
            log.error("Error", ex);
            TransactionInfo transactionInfo = transaction.getTransactionInfo();
            transactionInfo.setMessage(ApiErrorInfo.TRANSACTION_OPERATION_FAILED.getMessage());
            transactionInfo.setCode(ApiErrorInfo.TRANSACTION_OPERATION_FAILED.getCode());
            updatedTransactions.add(transaction);

            return OperationResult.<ProcessTransactionResponse>builder()
                    .addReturnedValue(new ProcessTransactionResponse(updatedTransactions, updatedAccounts))
                    .build();
        }
    }
}
