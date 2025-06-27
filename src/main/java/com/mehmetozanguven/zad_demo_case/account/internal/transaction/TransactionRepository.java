package com.mehmetozanguven.zad_demo_case.account.internal.transaction;

import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionStatus;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    @Lock(value = LockModeType.PESSIMISTIC_WRITE)
    @Query("FROM Transaction ts JOIN FETCH ts.account WHERE ts.id = :transactionId ")
    Optional<Transaction> findByPessimisticLockBeforeExpiration(String transactionId);

    @Query("FROM Transaction ts JOIN FETCH ts.account WHERE ts.transactionStatus = :transactionStatus ")
    List<Transaction> getTransactionsInStatus(Pageable pageable, TransactionStatus transactionStatus);

    @Query("FROM Transaction ts JOIN FETCH ts.account WHERE ts.account.id = :accountId AND ts.transactionStatus = :transactionStatus")
    List<Transaction> getListOfTransactionForAccount(String accountId, TransactionStatus transactionStatus);
}
