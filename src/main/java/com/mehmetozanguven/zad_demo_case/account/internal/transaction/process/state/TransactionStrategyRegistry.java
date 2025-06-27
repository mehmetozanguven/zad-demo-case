package com.mehmetozanguven.zad_demo_case.account.internal.transaction.process.state;

import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class TransactionStrategyRegistry {

    private final Map<TransactionStatus, TransactionStateStrategy> strategyMap = new EnumMap<>(TransactionStatus.class);
    private final TransactionStateStrategy emptyTransactionStrategy;

    @Autowired
    public TransactionStrategyRegistry(List<TransactionStateStrategy> strategies, EmptyTransactionStrategy emptyTransactionStrategy) {
        this.emptyTransactionStrategy = emptyTransactionStrategy;
        for (TransactionStateStrategy strategy : strategies) {
            TransactionStatusHandler annotation = strategy.getClass().getAnnotation(TransactionStatusHandler.class);
            if (annotation != null) {
                strategyMap.put(annotation.value(), strategy);
            }
        }
    }

    public TransactionStateStrategy getHandler(TransactionStatus type) {
        return Optional.ofNullable(strategyMap.get(type))
                .orElse(emptyTransactionStrategy);
    }
}
