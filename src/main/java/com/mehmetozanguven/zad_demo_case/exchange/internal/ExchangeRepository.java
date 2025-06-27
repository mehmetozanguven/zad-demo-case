package com.mehmetozanguven.zad_demo_case.exchange.internal;

import com.mehmetozanguven.zad_demo_case.core.converter.exchange.ExchangeType;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface ExchangeRepository extends JpaRepository<Exchange, String> {
    @Lock(LockModeType.PESSIMISTIC_READ)
    @Query("FROM Exchange ex WHERE ex.id = :id AND ex.expirationTime > :offsetNowAsUTC")
    Optional<Exchange> findExchangeRateWithPessimisticLock(String id, OffsetDateTime offsetNowAsUTC);

    @Query("FROM Exchange ex WHERE ex.exchangeType = :exchangeType AND ex.expirationTime > :time")
    Slice<Exchange> findExchangeByType(ExchangeType exchangeType, OffsetDateTime time, Pageable pageable);
}
