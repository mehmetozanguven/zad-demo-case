package com.mehmetozanguven.zad_demo_case.account.internal.account;

import com.mehmetozanguven.zad_demo_case.account.AccountModel;
import com.mehmetozanguven.zad_demo_case.account.CurrentBalance;
import com.mehmetozanguven.zad_demo_case.core.ApiApplyOperationResultLogic;
import com.mehmetozanguven.zad_demo_case.core.BusinessUseCase;
import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.commonModel.ApiBaseModel;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialAmount;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.converter.currency.CurrencyType;
import com.mehmetozanguven.zad_demo_case.core.converter.exchange.ExchangeType;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiErrorInfo;
import com.mehmetozanguven.zad_demo_case.exchange.ExchangeResult;
import com.mehmetozanguven.zad_demo_case.exchange.ExchangeService;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@BusinessUseCase
@RequiredArgsConstructor
public class GetAccountBalanceInGivenCurrencyUseCase implements ApiApplyOperationResultLogic<GetAccountBalanceInGivenCurrencyUseCase.AccountBalanceInGivenCurrencyRequest, GetAccountBalanceInGivenCurrencyUseCase.AccountBalanceInGivenCurrencyRequest, CurrentBalance> {
    @Builder
    public record AccountBalanceInGivenCurrencyRequest(
            List<AccountModel> accounts,
            CurrencyType currencyType
    ) {}

    private final ExchangeService exchangeService;

    @Override
    public OperationResult<AccountBalanceInGivenCurrencyRequest> logicBefore(AccountBalanceInGivenCurrencyRequest request) {
        if (CollectionUtils.isEmpty(request.accounts())) {
            return OperationResult.<AccountBalanceInGivenCurrencyRequest>builder()
                    .addException(ApiErrorInfo.ACCOUNT_OPERATION_FAILED)
                    .build();
        }
        long distinctUserIds = request.accounts().stream().map(AccountModel::getUserId).distinct().count();
        if (distinctUserIds > 1) {
            return OperationResult.<AccountBalanceInGivenCurrencyRequest>builder()
                    .addException(ApiErrorInfo.ACCOUNT_OPERATION_FAILED)
                    .build();
        }
        return OperationResult.<AccountBalanceInGivenCurrencyRequest>builder()
                .addReturnedValue(request)
                .build();
    }

    @Override
    public OperationResult<CurrentBalance> executeLogic(AccountBalanceInGivenCurrencyRequest request) {
        Map<CurrencyType, List<AccountModel>> accountsPerType = request.accounts().stream()
                .collect(Collectors.groupingBy(model -> model.getBalance().currency(), Collectors.toList()));
        BigDecimal totalBalance = BigDecimal.ZERO;
        List<String> accountIds = request.accounts().stream().map(ApiBaseModel::getId).toList();
        for (Map.Entry<CurrencyType, List<AccountModel>> each : accountsPerType.entrySet()) {
            if (request.currencyType().equals(each.getKey())) {
                BigDecimal totalBalanceInAccount = each.getValue().stream().map(accountModel -> accountModel.getBalance().amount().givenAmount())
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                totalBalance = totalBalance.add(totalBalanceInAccount);
            } else {
                ExchangeType exchangeType = ExchangeType.createExchangeType(each.getKey(), request.currencyType());

                BigDecimal totalBalanceInDifferentCurrencies = each.getValue().stream().map(accountModel -> {
                    ExchangeResult exchangeResult = exchangeService.getExchange(exchangeType, accountModel.getBalance());
                    return exchangeResult.financialMoney().amount().givenAmount();
                }).reduce(BigDecimal.ZERO, BigDecimal::add);

                totalBalance = totalBalance.add(totalBalanceInDifferentCurrencies);
            }
        }
        FinancialMoney totalMoney = new FinancialMoney(new FinancialAmount(totalBalance), request.currencyType());
        return OperationResult.<CurrentBalance>builder()
                .addReturnedValue(new CurrentBalance(accountIds, totalMoney))
                .build();
    }

    @Override
    public void afterExecution(OperationResult<CurrentBalance> response) {

    }
}
