package com.mehmetozanguven.zad_demo_case.account;

import com.mehmetozanguven.zad_swagger_api.contract.openapi.api.controller.TransactionControllerApi;
import com.mehmetozanguven.zad_swagger_api.contract.openapi.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class TransactionController implements TransactionControllerApi {
    private final TransactionService transactionService;

    @Override
    public TransactionApiResponse doDepositOperation(DepositRequest depositRequest) {
        TransactionResponse transactionResponse = transactionService.createDepositTransaction(depositRequest);
        return new TransactionApiResponse()
                .isSuccess(true)
                .httpStatusCode(HttpStatus.CREATED.value())
                .response(transactionResponse);
    }

    @Override
    public TransactionApiResponse doExchangeBetweenAccounts(ExchangeRequest exchangeRequest) {
        TransactionResponse transactionResponse = transactionService.createExchangeRequest(exchangeRequest);
        return new TransactionApiResponse()
                .isSuccess(true)
                .httpStatusCode(HttpStatus.CREATED.value())
                .response(transactionResponse);
    }

    @Override
    public TransactionApiResponse doWithdrawOperation(WithdrawRequest withdrawRequest) {
        TransactionResponse transactionResponse = transactionService.createWithdrawTransaction(withdrawRequest);
        return new TransactionApiResponse()
                .isSuccess(true)
                .httpStatusCode(HttpStatus.CREATED.value())
                .response(transactionResponse);
    }

    @Override
    public TransactionApiResponse doGetTransactionStatus(String transactionId) {
        TransactionResponse transactionResponse = transactionService.getTransactionById(transactionId);
        return new TransactionApiResponse()
                .isSuccess(true)
                .httpStatusCode(HttpStatus.OK.value())
                .response(transactionResponse);
    }
}
