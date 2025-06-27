package com.mehmetozanguven.zad_demo_case.account.internal.transaction;

import com.mehmetozanguven.zad_demo_case.account.TransactionModel;
import com.mehmetozanguven.zad_demo_case.account.internal.account.Account;
import com.mehmetozanguven.zad_demo_case.account.internal.account.AccountRepository;
import com.mehmetozanguven.zad_demo_case.core.BaseApplicationModuleTest;
import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialAmount;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.converter.AccountType;
import com.mehmetozanguven.zad_demo_case.core.converter.currency.CurrencyType;
import com.mehmetozanguven.zad_demo_case.core.converter.exchange.ExchangeType;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionStatus;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionType;
import com.mehmetozanguven.zad_demo_case.core.dummyExchangeService.TestExchangeServiceConfiguration;
import com.mehmetozanguven.zad_demo_case.exchange.ExchangeService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.modulith.test.ApplicationModuleTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Import(value = TestExchangeServiceConfiguration.class)
@ApplicationModuleTest(
        mode = ApplicationModuleTest.BootstrapMode.ALL_DEPENDENCIES,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class CreateNewTransactionUseCaseTest extends BaseApplicationModuleTest {
    @Autowired
    CreateNewTransactionUseCase createNewTransactionUseCase;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    ExchangeService exchangeService;

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
    void applyBusiness_ShouldReturnError_WhenAccountNotFound() {
        var request = new CreateNewTransactionUseCase.CreateTransactionRequest("abc", "adf", TransactionType.DEPOSIT, new FinancialMoney(FinancialAmount.ZERO, CurrencyType.USD), null);
        OperationResult<TransactionModel> result = createNewTransactionUseCase.applyBusiness(request);
        Assertions.assertFalse(result.isValid());
    }

    @Test
    void applyBusiness_ShouldTransactionTypeMustBeWaiting_WhenNewTransactionCreated() {
        List<Account> newAccounts = List.of(
                Account.builder()
                        .accountType(AccountType.TRY)
                        .userId("abc")
                        .balance(FinancialAmount.ZERO.givenAmount())
                        .currencyType(CurrencyType.TRY)
                        .build()
        );
        newAccounts = accountRepository.saveAll(newAccounts);
        var request = new CreateNewTransactionUseCase.CreateTransactionRequest("abc", newAccounts.getFirst().getId(), TransactionType.DEPOSIT, new FinancialMoney(new FinancialAmount(BigDecimal.valueOf(50)), CurrencyType.TRY), null);
        OperationResult<TransactionModel> result = createNewTransactionUseCase.applyBusiness(request);
        Assertions.assertAll(
                () -> Assertions.assertTrue(result.isValid()),
                () -> Assertions.assertEquals(TransactionStatus.WAITING, result.getReturnedValue().getTransactionStatus()),
                () -> Assertions.assertEquals(FinancialAmount.ZERO.givenAmount(), result.getReturnedValue().getAccountModel().getBalance().amount().givenAmount()),
                () -> Assertions.assertNotNull(result.getReturnedValue().getExpirationTime()),
                () -> Assertions.assertNull(result.getReturnedValue().getProcessedAt())
        );
    }

    @Test
    void applyBusiness_ShouldTransactionBeTheSame_WhenMatchedWithAccountType() {
        UserAndItsAccount userAndItsAccount = createUser();

        // add transaction with TRY into TRY account
        FinancialMoney tryTransaction = new FinancialMoney(new FinancialAmount(BigDecimal.valueOf(50)), CurrencyType.TRY);
        var request = new CreateNewTransactionUseCase.CreateTransactionRequest(userAndItsAccount.userId(),
                userAndItsAccount.accounts().get(AccountType.TRY), TransactionType.DEPOSIT, tryTransaction, null);

        OperationResult<TransactionModel> result = createNewTransactionUseCase.applyBusiness(request);
        Assertions.assertAll(
                () -> Assertions.assertTrue(result.isValid()),
                () -> Assertions.assertEquals(tryTransaction.currency(), result.getReturnedValue().getAmount().currency()),
                () -> Assertions.assertEquals(tryTransaction.amount().givenAmount(), result.getReturnedValue().getAmount().amount().givenAmount()),
                () -> Assertions.assertEquals(FinancialAmount.ZERO.givenAmount(), result.getReturnedValue().getAccountModel().getBalance().amount().givenAmount())
        );
    }

    @Test
    void applyBusiness_ShouldTransactionAmountMustBeMatchedWithAccountType() {
        UserAndItsAccount userAndItsAccount = createUser();

        // add transaction with TRY but into USD account
        FinancialMoney tryTransaction = new FinancialMoney(new FinancialAmount(BigDecimal.valueOf(50)), CurrencyType.TRY);
        FinancialMoney expectedTransactionMoneyAfterSaved = exchangeService.getExchange(ExchangeType.TRY_USD, tryTransaction).financialMoney();
        var request = new CreateNewTransactionUseCase.CreateTransactionRequest(userAndItsAccount.userId(),
                userAndItsAccount.accounts().get(AccountType.USD), TransactionType.DEPOSIT, tryTransaction, null);
        OperationResult<TransactionModel> result = createNewTransactionUseCase.applyBusiness(request);
        Assertions.assertAll(
                () -> Assertions.assertTrue(result.isValid()),
                () -> Assertions.assertEquals(expectedTransactionMoneyAfterSaved.currency(), result.getReturnedValue().getAmount().currency()),
                () -> Assertions.assertEquals(expectedTransactionMoneyAfterSaved.amount().givenAmount(), result.getReturnedValue().getAmount().amount().givenAmount()),
                () -> Assertions.assertEquals(FinancialAmount.ZERO.givenAmount(), result.getReturnedValue().getAccountModel().getBalance().amount().givenAmount())
        );

        // add transaction with USD but into TRY account
        FinancialMoney usdTransaction = new FinancialMoney(new FinancialAmount(BigDecimal.valueOf(50)), CurrencyType.USD);
        FinancialMoney expectedUSDTransactionMoneyAfterSaved = exchangeService.getExchange(ExchangeType.USD_TRY, usdTransaction).financialMoney();
        var usdRequest = new CreateNewTransactionUseCase.CreateTransactionRequest(userAndItsAccount.userId(),
                userAndItsAccount.accounts().get(AccountType.TRY), TransactionType.WITHDRAW, usdTransaction, null);
        OperationResult<TransactionModel> usdResult = createNewTransactionUseCase.applyBusiness(usdRequest);
        Assertions.assertAll(
                () -> Assertions.assertTrue(usdResult.isValid()),
                () -> Assertions.assertEquals(expectedUSDTransactionMoneyAfterSaved.currency(), usdResult.getReturnedValue().getAmount().currency()),
                () -> Assertions.assertEquals(expectedUSDTransactionMoneyAfterSaved.amount().givenAmount(), usdResult.getReturnedValue().getAmount().amount().givenAmount()),
                () -> Assertions.assertEquals(FinancialAmount.ZERO.givenAmount(), usdResult.getReturnedValue().getAccountModel().getBalance().amount().givenAmount())
        );

    }

}