package com.mehmetozanguven.zad_demo_case.account.internal.transaction.process.state;

import com.mehmetozanguven.zad_demo_case.core.converter.transaction.TransactionStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface TransactionStatusHandler {
    TransactionStatus value();

}
