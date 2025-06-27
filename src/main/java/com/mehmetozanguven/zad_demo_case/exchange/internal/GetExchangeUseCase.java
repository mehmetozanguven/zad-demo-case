package com.mehmetozanguven.zad_demo_case.exchange.internal;

import com.mehmetozanguven.zad_demo_case.core.ApiApplyOperationResultLogic;
import com.mehmetozanguven.zad_demo_case.core.BusinessUseCase;
import com.mehmetozanguven.zad_demo_case.core.DateOperation;
import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.converter.exchange.ExchangeType;
import com.mehmetozanguven.zad_demo_case.exchange.ExchangeModel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;

import java.time.Duration;
import java.util.Optional;

@BusinessUseCase
@RequiredArgsConstructor
public class GetExchangeUseCase implements ApiApplyOperationResultLogic<GetExchangeUseCase.GetExchangeRequest, ExchangeModel, ExchangeModel> {

    public record GetExchangeRequest(ExchangeType exchangeType) {}
    private final ExchangeApi exchangeApi;
    private final ExchangeRepository exchangeRepository;
    private final ExchangeMapper exchangeMapper;

    @Override
    public OperationResult<ExchangeModel> logicBefore(GetExchangeRequest request) {
        Slice<Exchange> inDb = exchangeRepository.findExchangeByType(request.exchangeType(), DateOperation.getOffsetNowAsUTC(), PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createTimestampMilli")));
        if (inDb.hasContent()) {
            return OperationResult.<ExchangeModel>builder()
                    .addReturnedValue(exchangeMapper.fromEntity(inDb.getContent().getFirst()))
                    .build();
        }
        OperationResult<ExchangeApiResult> apiResult = exchangeApi.getCurrency(request.exchangeType());
        apiResult.validateResult();
        ExchangeApiResult exchangeApiResult = apiResult.getReturnedValue();

        ExchangeModel newExchange = ExchangeModel.builder()
                .exchangeType(exchangeApiResult.exchangeType())
                .exchangeRate(exchangeApiResult.financialMoney().amount())
                .expirationTime(DateOperation.addDurationToUTCNow(Duration.ofMinutes(1)))
                .build();
        return OperationResult.<ExchangeModel>builder()
                .addReturnedValue(newExchange)
                .build();
    }

    @Override
    public OperationResult<ExchangeModel> executeLogic(ExchangeModel exchangeModel) {
        if (StringUtils.isNotBlank(exchangeModel.getId())) {
            return OperationResult.<ExchangeModel>builder()
                    .addReturnedValue(exchangeModel)
                    .build();
        }
        Exchange newExchange = exchangeMapper.fromModel(exchangeModel);
        newExchange = exchangeRepository.save(newExchange);
        return OperationResult.<ExchangeModel>builder()
                .addReturnedValue(exchangeMapper.fromEntity(newExchange))
                .build();
    }

    @Override
    public void afterExecution(OperationResult<ExchangeModel> response) {

    }

}
