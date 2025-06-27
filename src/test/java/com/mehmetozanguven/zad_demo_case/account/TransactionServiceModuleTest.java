package com.mehmetozanguven.zad_demo_case.account;

import com.mehmetozanguven.zad_demo_case.account.internal.account.Account;
import com.mehmetozanguven.zad_demo_case.account.internal.account.AccountRepository;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.Transaction;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.TransactionRepository;
import com.mehmetozanguven.zad_demo_case.core.BaseApplicationModuleTest;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialAmount;
import com.mehmetozanguven.zad_demo_case.core.converter.AccountType;
import com.mehmetozanguven.zad_demo_case.core.converter.currency.CurrencyType;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionStatus;
import com.mehmetozanguven.zad_swagger_api.contract.openapi.model.*;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.modulith.test.ApplicationModuleTest;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@ApplicationModuleTest(
        mode = ApplicationModuleTest.BootstrapMode.ALL_DEPENDENCIES,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class TransactionServiceModuleTest extends BaseApplicationModuleTest {
    @Autowired
    TransactionService transactionService;
    @Autowired
    AccountRepository accountRepository;

    @Autowired
    TransactionRepository transactionRepository;

    private record UserAndItsAccount(String userId, Map<AccountType, String> accounts) {};

    private UserAndItsAccount createUser() {
        List<Account> newAccounts = List.of(
                Account.builder()
                        .accountType(AccountType.TRY)
                        .userId("abc")
                        .balance(FinancialAmount.ZERO.givenAmount())
                        .currencyType(CurrencyType.TRY)
                        .build(),
                Account.builder()
                        .accountType(AccountType.USD)
                        .userId("abc")
                        .balance(FinancialAmount.ZERO.givenAmount())
                        .currencyType(CurrencyType.USD)
                        .build()
        );
        newAccounts = accountRepository.saveAll(newAccounts);
        Map<AccountType, String> accounts = Map.of(
                AccountType.TRY, newAccounts.stream().filter(account -> AccountType.TRY == account.getAccountType()).findFirst().orElseThrow().getId(),
                AccountType.USD, newAccounts.stream().filter(account -> AccountType.USD == account.getAccountType()).findFirst().orElseThrow().getId()
        );
        return new UserAndItsAccount("abc", accounts);
    }

    @Test
    void testConcurrentWithdrawOperations() throws InterruptedException {
        // only one of the transaction must be success,

        Account userAccount = Account.builder()
                .accountType(AccountType.TRY)
                .userId("abc")
                .balance(new FinancialAmount(BigDecimal.valueOf(100)).givenAmount())
                .currencyType(CurrencyType.TRY)
                .build();
        userAccount = accountRepository.save(userAccount);

        int numberOfThreads = 10;
        BigDecimal withdrawAmount = BigDecimal.valueOf(100);

        WithdrawRequest withdrawRequest = new WithdrawRequest()
                .accountId(new AppTextValue(userAccount.getId()))
                .userId(new AppTextValue("abc"))
                .money(new AppMoney().money(withdrawAmount).currencyType(AppCurrencyCode.TRY))
                ;
        try (ExecutorService executor = Executors.newFixedThreadPool(numberOfThreads)) {
            CountDownLatch latch = new CountDownLatch(numberOfThreads);

            for (int i = 0; i < numberOfThreads; i++) {
                executor.submit(() -> {
                    try {
                        transactionService.createWithdrawTransaction(withdrawRequest);
                    } catch (Exception e) {
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await(); // wait for all threads
            executor.shutdown();
        }


        String accountId = userAccount.getId();

        // wait for kafka operations and verify
        Awaitility.await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    Account account = accountRepository.findUserAccount("abc", accountId).orElseThrow();
                    List<Transaction> waitingTxs = transactionRepository.getListOfTransactionForAccount(account.getId(), TransactionStatus.WAITING);
                    List<Transaction> failTxs = transactionRepository.getListOfTransactionForAccount(account.getId(), TransactionStatus.FAIL);
                    List<Transaction> successTxs = transactionRepository.getListOfTransactionForAccount(account.getId(), TransactionStatus.SUCCESS);
                    List<Transaction> unknownTxs = transactionRepository.getListOfTransactionForAccount(account.getId(), TransactionStatus.UNKNOWN);

                    Assertions.assertAll(
                            () -> Assertions.assertEquals(FinancialAmount.ZERO.givenAmount(), account.getBalance().amount().givenAmount()),
                            () -> Assertions.assertEquals(0, waitingTxs.size()),
                            () -> Assertions.assertEquals(numberOfThreads - 1, failTxs.size()),
                            () -> Assertions.assertEquals(1, successTxs.size()),
                            () -> Assertions.assertEquals(0, unknownTxs.size())
                    );
                });
    }

    @Test
    void createDepositTransaction_ShouldAddMoneyToUserAccount() {
        UserAndItsAccount userAndItsAccount = createUser();
        DepositRequest depositRequest = new DepositRequest()
                .userId(new AppTextValue(userAndItsAccount.userId))
                .money(new AppMoney(BigDecimal.valueOf(100), AppCurrencyCode.TRY))
                .accountId(new AppTextValue(userAndItsAccount.accounts().get(AccountType.TRY)))
                ;
        TransactionResponse transactionResponse = transactionService.createDepositTransaction(depositRequest);
        Assertions.assertAll(
                () -> Assertions.assertNotNull(transactionResponse.getTransactionId()),
                () -> Assertions.assertEquals(TransactionStatus.WAITING.value, transactionResponse.getTransactionStatus().getValue()),
                () -> Assertions.assertEquals(new FinancialAmount(BigDecimal.valueOf(100)).givenAmount(), transactionResponse.getAmount().getMoney()),
                () -> Assertions.assertEquals(AppCurrencyCode.TRY, transactionResponse.getAmount().getCurrencyType())
        );
        Awaitility.await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(Duration.ofSeconds(20))
                .untilAsserted(() -> {
                    TransactionResponse updatedTransaction = transactionService.getTransactionById(transactionResponse.getTransactionId());
                    Assertions.assertAll(
                            () -> Assertions.assertEquals(TransactionStatus.SUCCESS.getValue(), updatedTransaction.getTransactionStatus().getValue()),
                            () -> Assertions.assertEquals(new FinancialAmount(BigDecimal.valueOf(100.00)).givenAmount(), updatedTransaction.getAmount().getMoney()),
                            () -> Assertions.assertEquals(AppCurrencyCode.TRY, updatedTransaction.getAmount().getCurrencyType())
                    );
                });
    }
}