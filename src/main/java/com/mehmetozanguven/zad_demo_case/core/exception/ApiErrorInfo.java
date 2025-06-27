package com.mehmetozanguven.zad_demo_case.core.exception;

import org.springframework.http.HttpStatus;

import java.util.Objects;

public enum ApiErrorInfo implements ApiExceptionInfo {
    SOMETHING_WENT_WRONG(
            "A-1",
            "Something went wrong",
            HttpStatus.OK
    ),
    CONSTRAINT_VIOLATION_ERROR(
            "A-2",
            "Something went wrong",
            HttpStatus.BAD_REQUEST
    ),
    METHOD_ARGUMENT_NOT_VALID_ERROR(
            "A-3",
            "Something went wrong",
            HttpStatus.BAD_REQUEST
    ),
    INVALID_REQUEST(
            "A-4",
            "Request is invalid",
            HttpStatus.OK
    ),
    USER_ALREADY_EXISTS(
            "A-4",
            "User already exists",
            HttpStatus.OK
    ),
    ACCOUNT_OPERATION_FAILED(
            "A-5",
            "Account operation failed",
            HttpStatus.OK
    ),
    EXCHANGE_OPERATION_FAILED("A-6", "Exchange operation failed", HttpStatus.BAD_REQUEST),
    TRANSACTION_OPERATION_FAILED("A-7", "Transaction operation failed", HttpStatus.OK),
    TRANSACTION_NOT_FOUND("A-8", "Transaction operation failed", HttpStatus.OK),
    TRANSACTION_ALREADY_PROCESSED("A-9", "Transaction operation failed", HttpStatus.OK)

    ;

    public final String code;
    public final String message;
    public final HttpStatus httpStatus;

    ApiErrorInfo(String code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public HttpStatus getExceptionStatus() {
        if (Objects.nonNull(httpStatus)) {
            return httpStatus;
        }
        return ApiExceptionInfo.super.getExceptionStatus();
    }


    public String toString() {
        return "ApiErrorInformation{code=" + this.code + ", message='" + this.message + "'}";
    }
}
