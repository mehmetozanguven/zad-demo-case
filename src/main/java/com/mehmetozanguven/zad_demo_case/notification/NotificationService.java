package com.mehmetozanguven.zad_demo_case.notification;

import com.mehmetozanguven.zad_demo_case.core.commonModel.TransactionEvent;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {
    private final EmailService emailService;

    @KafkaListener(topics = {"${app.kafka.transactionTopic}"}, groupId = "${app.kafka.notificationGroupId}", concurrency = "1", containerFactory = "transactionEventConcurrentKafkaListenerContainerFactory")
    public void listenTransactionEvent(@Payload @Valid TransactionEvent transactionEvent, Acknowledgment ack) {
        log.info("NotificationService with id: {} in thread: {}\n\n”", transactionEvent.transactionId(), Thread.currentThread().getName());
        if (TransactionStatus.FAIL.equals(transactionEvent.transactionStatus()) || TransactionStatus.SUCCESS.equals(transactionEvent.transactionStatus())) {
            // get error information from transaction module (by using id of the transaction) and send email to the user
            emailService.sendEmail();
        }
        ack.acknowledge();
    }
//
//    @EventListener
//    public void triggerNotificationAboutTransaction(TransactionEvent transactionEvent) {
//        if (TransactionStatus.FAIL.equals(transactionEvent.transactionStatus()) || TransactionStatus.SUCCESS.equals(transactionEvent.transactionStatus())) {
//            // get error information from transaction module (by using id of the transaction) and send email to the user
//            emailService.sendEmail();
//        }
//    }
}
