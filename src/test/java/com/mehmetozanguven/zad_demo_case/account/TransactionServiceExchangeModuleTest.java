package com.mehmetozanguven.zad_demo_case.account;

import com.mehmetozanguven.zad_demo_case.account.internal.account.Account;
import com.mehmetozanguven.zad_demo_case.account.internal.account.AccountRepository;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.Transaction;
import com.mehmetozanguven.zad_demo_case.account.internal.transaction.TransactionRepository;
import com.mehmetozanguven.zad_demo_case.core.BaseApplicationModuleTest;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialAmount;
import com.mehmetozanguven.zad_demo_case.core.converter.AccountType;
import com.mehmetozanguven.zad_demo_case.core.converter.SwaggerAppConverter;
import com.mehmetozanguven.zad_demo_case.core.converter.currency.CurrencyType;
import com.mehmetozanguven.zad_demo_case.core.converter.exchange.ExchangeType;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionStatus;
import com.mehmetozanguven.zad_demo_case.core.dummyExchangeService.TestExchangeServiceConfiguration;
import com.mehmetozanguven.zad_demo_case.exchange.ExchangeService;
import com.mehmetozanguven.zad_demo_case.user.internal.User;
import com.mehmetozanguven.zad_swagger_api.contract.openapi.model.*;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.ApplicationModuleTest;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Import(value = TestExchangeServiceConfiguration.class)
@ApplicationModuleTest(
        mode = ApplicationModuleTest.BootstrapMode.ALL_DEPENDENCIES,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class TransactionServiceExchangeModuleTest extends BaseApplicationModuleTest {
    @Autowired
    TransactionService transactionService;
    @Autowired
    AccountRepository accountRepository;

    private record UserAndItsAccount(Map<AccountType, Account> accounts) {};

    private Map<String, UserAndItsAccount> createTwoUsers() {
        List<Account> newAccounts = List.of(
                Account.builder()
                        .accountType(AccountType.TRY)
                        .userId("user-1")
                        .balance(new FinancialAmount(BigDecimal.valueOf(100)).givenAmount())
                        .currencyType(CurrencyType.TRY)
                        .build(),
                Account.builder()
                        .accountType(AccountType.USD)
                        .userId("user-1")
                        .balance(new FinancialAmount(BigDecimal.valueOf(100)).givenAmount())
                        .currencyType(CurrencyType.USD)
                        .build(),
                Account.builder()
                        .accountType(AccountType.TRY)
                        .userId("user-2")
                        .balance(new FinancialAmount(BigDecimal.valueOf(100)).givenAmount())
                        .currencyType(CurrencyType.TRY)
                        .build(),
                Account.builder()
                        .accountType(AccountType.USD)
                        .userId("user-2")
                        .balance(new FinancialAmount(BigDecimal.valueOf(100)).givenAmount())
                        .currencyType(CurrencyType.USD)
                        .build()
        );
        accountRepository.saveAll(newAccounts);
        Set<Account> user1Accounts = accountRepository.getUserAccounts("user-1");
        Map<AccountType, Account> user1AccountsMap = Map.of(
                AccountType.TRY, user1Accounts.stream().filter(account -> AccountType.TRY == account.getAccountType()).findFirst().orElseThrow(),
                AccountType.USD, user1Accounts.stream().filter(account -> AccountType.USD == account.getAccountType()).findFirst().orElseThrow()
        );

        Set<Account> user2Accounts = accountRepository.getUserAccounts("user-2");
        Map<AccountType, Account> user2AccountsMap = Map.of(
                AccountType.TRY, user2Accounts.stream().filter(account -> AccountType.TRY == account.getAccountType()).findFirst().orElseThrow(),
                AccountType.USD, user2Accounts.stream().filter(account -> AccountType.USD == account.getAccountType()).findFirst().orElseThrow()
        );

        return Map.of(
                "user-1", new UserAndItsAccount(user1AccountsMap),
                "user-2", new UserAndItsAccount(user2AccountsMap)
        );
    }


    @Test
    void createExchangeTransaction_ShouldAddMoneyToDifferentAccount() {
        Map<String, UserAndItsAccount> userMaps = createTwoUsers();
        // send usd from user_1 to user_2 usd account
        ExchangeRequest exchangeRequestFrom_User_1_USD_to_User_2_USD = new ExchangeRequest()
                .fromUserId(new AppTextValue("user-1"))
                .fromAccountId(new AppTextValue(userMaps.get("user-1").accounts().get(AccountType.USD).getId()))
                .money(new AppMoney().money(BigDecimal.valueOf(10)).currencyType(AppCurrencyCode.USD))
                .toAccountId(new AppTextValue(userMaps.get("user-2").accounts().get(AccountType.USD).getId()))
                ;
        TransactionResponse transactionResponse = transactionService.createExchangeRequest(exchangeRequestFrom_User_1_USD_to_User_2_USD);
        Assertions.assertAll(
                () -> Assertions.assertNotNull(transactionResponse.getTransactionId()),
                () -> Assertions.assertEquals(TransactionStatus.WAITING.value, transactionResponse.getTransactionStatus().getValue())
        );
        Awaitility.await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(Duration.ofSeconds(30))
                .untilAsserted(() -> {
                    TransactionResponse updatedTransaction = transactionService.getTransactionById(transactionResponse.getTransactionId());
                    Account fromAccount = accountRepository.findUserAccountByAccountId(userMaps.get("user-1").accounts().get(AccountType.USD).getId()).orElseThrow();
                    Account toAccount = accountRepository.findUserAccountByAccountId(userMaps.get("user-2").accounts().get(AccountType.USD).getId()).orElseThrow();
                    Assertions.assertAll(
                            () -> Assertions.assertEquals(TransactionStatus.SUCCESS.getValue(), updatedTransaction.getTransactionStatus().getValue()),
                            () -> Assertions.assertEquals(new FinancialAmount(BigDecimal.valueOf(10)).givenAmount(), updatedTransaction.getAmount().getMoney()),
                            () -> Assertions.assertEquals(AppCurrencyCode.USD, updatedTransaction.getAmount().getCurrencyType()),
                            () -> Assertions.assertEquals(new FinancialAmount(BigDecimal.valueOf(90)).givenAmount(), fromAccount.getBalance().amount().givenAmount()),
                            () -> Assertions.assertEquals(new FinancialAmount(BigDecimal.valueOf(110)).givenAmount(), toAccount.getBalance().amount().givenAmount())
                    );
                });

        // send TRY from user_2 TRY to user_1 USD account
        ExchangeRequest exchangeRequestFrom_User_2_TRY_to_User_1_USD = new ExchangeRequest()
                .fromUserId(new AppTextValue("user-2"))
                .fromAccountId(new AppTextValue(userMaps.get("user-2").accounts().get(AccountType.TRY).getId()))
                .money(new AppMoney().money(BigDecimal.valueOf(100)).currencyType(AppCurrencyCode.TRY))
                .toAccountId(new AppTextValue(userMaps.get("user-1").accounts().get(AccountType.USD).getId()))
                ;

        TransactionResponse transactionResponse1 = transactionService.createExchangeRequest(exchangeRequestFrom_User_2_TRY_to_User_1_USD);

        Awaitility.await()
                .pollInterval(Duration.ofSeconds(3))
                .atMost(Duration.ofSeconds(20))
                .untilAsserted(() -> {
                    TransactionResponse updatedTransaction = transactionService.getTransactionById(transactionResponse1.getTransactionId());

                    Assertions.assertAll(
                            () -> Assertions.assertEquals(TransactionStatus.SUCCESS.getValue(), updatedTransaction.getTransactionStatus().getValue()),
                            () -> Assertions.assertEquals(AppCurrencyCode.TRY, updatedTransaction.getAmount().getCurrencyType())
                    );
                });
    }
}