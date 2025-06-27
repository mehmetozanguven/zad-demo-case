package com.mehmetozanguven.zad_demo_case.account.internal.transaction.process.type;

import com.mehmetozanguven.zad_demo_case.account.internal.AccountExchangeOperation;
import com.mehmetozanguven.zad_demo_case.account.internal.account.Account;
import com.mehmetozanguven.zad_demo_case.account.internal.account.AccountRepository;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.Transaction;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.TransactionInfo;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.process.ProcessTransactionResponse;
import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialAmount;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionStatus;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionType;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiErrorInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@TransactionTypeHandler(TransactionType.EXCHANGE)
public class ExchangeStrategy implements TransactionTypeStrategy {
    private final AccountRepository accountRepository;
    private final AccountExchangeOperation accountExchangeOperation;

    @Override
    public OperationResult<ProcessTransactionResponse> doAction(Transaction transaction) {
        List<Transaction> updatedTransactions = new ArrayList<>();
        List<Account> updatedAccounts = new ArrayList<>();
        TransactionInfo transactionInfo = transaction.getTransactionInfo();

        try {
            Account fromAccount = transaction.getAccount();
            Account toAccount  = accountRepository.findUserAccountByAccountId(transactionInfo.getTargetAccountId()).orElseThrow();

            if (StringUtils.equals(fromAccount.getId(), toAccount.getId())) {
                transaction.setTransactionStatus(TransactionStatus.FAIL);
                transactionInfo.setCode("-1");
                transactionInfo.setMessage("Exchange operation can only be done across different users");
                updatedTransactions.add(transaction);

                return OperationResult.<ProcessTransactionResponse>builder()
                        .addReturnedValue(new ProcessTransactionResponse(updatedTransactions, updatedAccounts))
                        .build();
            }


            FinancialMoney currentMoneyInFromAccount = fromAccount.getBalance();

            FinancialMoney transactionAmount = transaction.getAmount();

            FinancialAmount nextAmountFor_FromAccount = new FinancialAmount(currentMoneyInFromAccount.amount().givenAmount().subtract(transactionAmount.amount().givenAmount()));

            FinancialMoney currentMoneyInToAccount = toAccount.getBalance();

            FinancialMoney convertedNextAmountTransaction_to_ToAccount = accountExchangeOperation.getTransactionInAccountCurrency(toAccount.getCurrencyType(), transactionAmount);
            FinancialAmount nextAmountFor_ToAccount = new FinancialAmount(currentMoneyInToAccount.amount().givenAmount().add(convertedNextAmountTransaction_to_ToAccount.amount().givenAmount()));
            boolean isLessThanZero = FinancialAmount.isLessThanGivenAmount(nextAmountFor_FromAccount, FinancialAmount.ZERO);
            if (isLessThanZero) {
                transaction.setTransactionStatus(TransactionStatus.FAIL);
                transactionInfo.setCode("-1");
                transactionInfo.setMessage("Balance can not be less than zero");
            } else {
                transaction.setTransactionStatus(TransactionStatus.SUCCESS);
                fromAccount.setBalance(nextAmountFor_FromAccount);
                toAccount.setBalance(nextAmountFor_ToAccount);
                updatedAccounts.add(fromAccount);
                updatedAccounts.add(toAccount);
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
