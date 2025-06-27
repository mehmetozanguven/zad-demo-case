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
@TransactionTypeHandler(TransactionType.WITHDRAW)
public class WithdrawStrategy implements TransactionTypeStrategy {

    @Override
    public OperationResult<ProcessTransactionResponse> doAction(Transaction transaction) {
        List<Transaction> updatedTransactions = new ArrayList<>();
        List<Account> updatedAccounts = new ArrayList<>();
        TransactionInfo transactionInfo = transaction.getTransactionInfo();

        try {
            Account transactionAccount = transaction.getAccount();
            FinancialMoney currentMoney = transactionAccount.getBalance();

            FinancialAmount transactionAmount = transaction.getAmount().amount();

            FinancialAmount currentAmount = currentMoney.amount();
            FinancialAmount nextAmount = new FinancialAmount(currentAmount.givenAmount().subtract(transactionAmount.givenAmount()));

            boolean isLessThanZero = FinancialAmount.isLessThanGivenAmount(nextAmount, FinancialAmount.ZERO);
            if (isLessThanZero) {
                transaction.setTransactionStatus(TransactionStatus.FAIL);
                transactionInfo.setMessage("Balance can not be less than zero");
                transactionInfo.setCode(ApiErrorInfo.TRANSACTION_OPERATION_FAILED.getCode());
            } else {
                transaction.setTransactionStatus(TransactionStatus.SUCCESS);
                transactionAccount.setBalance(nextAmount);
                updatedAccounts.add(transactionAccount);
            }
            updatedTransactions.add(transaction);
            return OperationResult.<ProcessTransactionResponse>builder()
                    .addReturnedValue(new ProcessTransactionResponse(updatedTransactions, updatedAccounts))
                    .build();
        } catch (Exception ex) {
            log.error("Error", ex);
            transactionInfo.setMessage(ApiErrorInfo.TRANSACTION_OPERATION_FAILED.getMessage());
            transactionInfo.setCode(ApiErrorInfo.TRANSACTION_OPERATION_FAILED.getCode());
            updatedTransactions.add(transaction);

            return OperationResult.<ProcessTransactionResponse>builder()
                    .addReturnedValue(new ProcessTransactionResponse(updatedTransactions, updatedAccounts))
                    .build();
        }
    }
}
