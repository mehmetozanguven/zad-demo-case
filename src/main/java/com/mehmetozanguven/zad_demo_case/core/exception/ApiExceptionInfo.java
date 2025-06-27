package com.mehmetozanguven.zad_demo_case.core.exception;

import org.springframework.http.HttpStatus;

public interface ApiExceptionInfo {
    String getCode();

    String getMessage();

    default HttpStatus getExceptionStatus() {
        return HttpStatus.BAD_REQUEST;
    }
}
