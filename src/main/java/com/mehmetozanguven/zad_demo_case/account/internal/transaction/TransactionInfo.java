package com.mehmetozanguven.zad_demo_case.account.internal.transaction;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TransactionInfo {
    private String code;
    private String message;
    private String targetAccountId;
}
