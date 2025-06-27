package com.mehmetozanguven.zad_demo_case.account.internal.transaction;

import com.mehmetozanguven.zad_demo_case.core.commonModel.TransactionEvent;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class TransactionStatusChangeListener {

    private static TransactionKafkaService transactionKafkaService;

    @Autowired
    public void setPublisher(TransactionKafkaService transactionKafkaService) {
        TransactionStatusChangeListener.transactionKafkaService = transactionKafkaService;
    }

    @PostPersist
    public void triggerNewTransactionEvent_in_posPersist(Transaction entity) {
        log.info("New transaction event saved {}", entity.getId());
        String accountId = entity.getAccount().getId();
        TransactionEvent transactionEvent = new TransactionEvent(entity.getId(), entity.getTransactionStatus());
        transactionKafkaService.publishTransactionEvent(accountId, transactionEvent);
    }

    @PostUpdate
    public void triggerNewTransactionEvent_in_postUpdate(Transaction entity) {
        log.info("Update transaction event {}", entity.getId());
        String accountId = entity.getAccount().getId();
        TransactionEvent transactionEvent = new TransactionEvent(entity.getId(), entity.getTransactionStatus());
        transactionKafkaService.publishTransactionEvent(accountId, transactionEvent);
    }
}
