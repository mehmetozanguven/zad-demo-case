package com.mehmetozanguven.zad_demo_case.exchange.internal;

import com.mehmetozanguven.zad_demo_case.core.OperationResult;
import com.mehmetozanguven.zad_demo_case.core.converter.exchange.ExchangeType;

public interface ExchangeApi {

    OperationResult<ExchangeApiResult> getCurrency(ExchangeType exchangeType);

}
