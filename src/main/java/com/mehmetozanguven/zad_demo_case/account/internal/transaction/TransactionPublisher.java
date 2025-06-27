package com.mehmetozanguven.zad_demo_case.account.internal.transaction;

import com.mehmetozanguven.zad_demo_case.account.internal.account.Account;
import com.mehmetozanguven.zad_demo_case.core.DateOperation;
import com.mehmetozanguven.zad_demo_case.core.commonModel.TransactionEvent;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class TransactionPublisher {
    private final TransactionRepository transactionRepository;
    private final TransactionKafkaService transactionKafkaService;

    @Scheduled(cron = "${app.scheduler.process-waiting-transactions}") // every 5 seconds
    @SchedulerLock(name = "transactionPublishes", lockAtMostFor = "5m", lockAtLeastFor = "1m")
    public void pushWaitingTransactions() {
        log.info("Trying to process WAITING transactions");
        PageRequest pageRequest = PageRequest.of(0, 100, Sort.by(Sort.Direction.ASC, "createTimestampMilli"));
        List<Transaction> waitingTransactions = transactionRepository.getTransactionsInStatus(pageRequest, TransactionStatus.WAITING);
        waitingTransactions.forEach(transaction -> {
            Account account = transaction.getAccount();
            transactionKafkaService.publishTransactionEvent(account.getId(), new TransactionEvent(transaction.getId(), transaction.getTransactionStatus()));
        });
    }

}
