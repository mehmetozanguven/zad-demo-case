package com.mehmetozanguven.zad_demo_case.account.internal.transaction;

import com.mehmetozanguven.zad_demo_case.account.internal.account.Account;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialAmount;
import com.mehmetozanguven.zad_demo_case.core.commonModel.FinancialMoney;
import com.mehmetozanguven.zad_demo_case.core.converter.currency.CurrencyType;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionStatus;
import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionType;
import com.mehmetozanguven.zad_demo_case.core.entity.ApiBaseEntity;
import com.mehmetozanguven.zad_demo_case.core.entity.AuditableListener;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.TimeZoneColumn;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

@With
@Builder
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "account_transactions")
@EntityListeners({TransactionStatusChangeListener.class})
public class Transaction extends ApiBaseEntity {
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "account_id")
    private Account account;

    @NotNull
    @Column(name = "transaction_type")
    private TransactionType transactionType;

    @NotNull
    @Column(name = "transaction_status")
    private TransactionStatus transactionStatus;

    @NotNull
    @Column(name = "amount")
    private BigDecimal amount;

    @NotNull
    @Column(name = "currency_type")
    private CurrencyType currencyType;

    @CreatedDate
    @Column(name = "processed_date")
    @TimeZoneStorage(TimeZoneStorageType.COLUMN)
    @TimeZoneColumn(
            name = "processed_date_offset",
            columnDefinition = "smallint unsigned"
    )
    private OffsetDateTime processedAt;

    @CreatedDate
    @Column(name = "expiration_time")
    @TimeZoneStorage(TimeZoneStorageType.COLUMN)
    @TimeZoneColumn(
            name = "expiration_time_offset",
            columnDefinition = "smallint unsigned"
    )
    private OffsetDateTime expirationTime;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "info_field", columnDefinition = "jsonb")
    @Builder.Default
    private TransactionInfo transactionInfo = TransactionInfo.builder().build();

    public TransactionInfo getTransactionInfo() {
        if (Objects.isNull(transactionInfo)) {
            return TransactionInfo.builder().build();
        } else {
            return transactionInfo;
        }
    }

    public void setAmount(FinancialAmount financialAmount) {
        this.amount= financialAmount.givenAmount();
    }

    public FinancialMoney getAmount() {
        if (Objects.isNull(amount)) {
            return new FinancialMoney(FinancialAmount.ZERO, currencyType);
        } else {
            return new FinancialMoney(new FinancialAmount(amount), currencyType);
        }
    }
}
