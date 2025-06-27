package com.mehmetozanguven.zad_demo_case.exchange.internal;


import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialAmount;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.converter.exchange.ExchangeType;
import com.mehmetozanguven.zad_demo_case.core.entity.ApiBaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.TimeZoneColumn;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@With
@SuperBuilder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "exchanges")
public class Exchange extends ApiBaseEntity {
    @NotNull
    @Column(name = "exchange_type")
    private ExchangeType exchangeType;
    @NotNull
    @Column(name = "exchange_rate")
    private BigDecimal exchangeRate;

    @NotNull
    @CreatedDate
    @Column(name = "expiration_time")
    @TimeZoneStorage(TimeZoneStorageType.COLUMN)
    @TimeZoneColumn(
            name = "expiration_time_offset",
            columnDefinition = "smallint unsigned"
    )
    private OffsetDateTime expirationTime;

    public FinancialAmount getExchangeRate() {
        return new FinancialAmount(exchangeRate);
    }

    public void setExchangeRate(FinancialAmount financialAmount) {
        this.exchangeRate = financialAmount.givenAmount();
    }
}
