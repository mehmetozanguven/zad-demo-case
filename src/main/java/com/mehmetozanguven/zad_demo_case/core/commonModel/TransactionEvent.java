package com.mehmetozanguven.zad_demo_case.core.commonModel;

import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TransactionEvent(
        @NotBlank String transactionId,
        @NotNull TransactionStatus transactionStatus
) {
}
