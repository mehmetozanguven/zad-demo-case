package com.mehmetozanguven.zad_demo_case.exchange.internal;

import com.mehmetozanguven.zad_demo_case.core.ApiApplyOperationResultLogic;
import com.mehmetozanguven.zad_demo_case.core.BusinessUseCase;
import com.mehmetozanguven.zad_demo_case.core.DateOperation;
import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiErrorInfo;
import com.mehmetozanguven.zad_demo_case.exchange.ExchangeModel;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@BusinessUseCase
@RequiredArgsConstructor
public class ValidateGivenExchangeRateUseCase implements ApiApplyOperationResultLogic<ExchangeModel, ExchangeModel, ExchangeModel> {

    private final ExchangeRepository exchangeRepository;
    private final ExchangeMapper exchangeMapper;

    @Override
    public OperationResult<ExchangeModel> logicBefore(ExchangeModel exchangeModel) {
        Optional<Exchange> inDb = exchangeRepository.findExchangeRateWithPessimisticLock(exchangeModel.getId(), DateOperation.getOffsetNowAsUTC());
        if (inDb.isEmpty()) {
            return OperationResult.<ExchangeModel>builder()
                    .addException(ApiErrorInfo.SOMETHING_WENT_WRONG)
                    .build();
        }
        Exchange exchange = inDb.get();
        if (exchange.getEntityVersionId().compareTo(exchangeModel.getEntityVersionId()) != 0) {
            return OperationResult.<ExchangeModel>builder()
                    .addException(ApiErrorInfo.SOMETHING_WENT_WRONG)
                    .build();
        }
        return OperationResult.<ExchangeModel>builder()
                .addReturnedValue(exchangeMapper.fromEntity(exchange))
                .build();
    }

    @Override
    public OperationResult<ExchangeModel> executeLogic(ExchangeModel exchangeModel) {
        return OperationResult.<ExchangeModel>builder()
                .addReturnedValue(exchangeModel)
                .build();
    }

    @Override
    public void afterExecution(OperationResult<ExchangeModel> response) {

    }
}
