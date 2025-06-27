package com.mehmetozanguven.zad_demo_case.core;


import com.mehmetozanguven.zad_demo_case.core.exception.ApiErrorInfo;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiException;
import com.mehmetozanguven.zad_demo_case.core.exception.ApiProblemDetail;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.List;
import java.util.Locale;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ApiRestControllerAdvice extends ResponseEntityExceptionHandler {
    private static final String SOMETHING_WENT_WRONG = "Something went wrong";


    protected String tryToGetRequestUriFromWebRequest(WebRequest webRequest) {
        try {
            return ((ServletWebRequest) webRequest).getRequest().getRequestURI();
        } catch (Exception ex) {
            log.info("Can not get request uri from web request. Returning contextPath ...", ex);
            return webRequest.getContextPath();
        }
    }


    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<Object> constraintViolationException(ConstraintViolationException ex, WebRequest request) {
        log.error("ConstraintViolationException for the following url :: {}", tryToGetRequestUriFromWebRequest(request), ex);

        URI instance = URI.create(tryToGetRequestUriFromWebRequest(request));
        ApiException apiException = new ApiException(ApiErrorInfo.CONSTRAINT_VIOLATION_ERROR);
        ApiProblemDetail problemDetail = ApiProblemDetail.forException(apiException, instance);


        GenericApiResponse<String> response = new GenericApiResponse.Builder<String>()
                .isSuccess(false)
                .httpStatusCode(apiException.getExceptionStatus())
                .addErrorResponse(problemDetail)
                .build();
        return new ResponseEntity<>(response, response.httpStatus);
    }


    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error("handleMethodArgumentNotValid for the following url :: {}", tryToGetRequestUriFromWebRequest(request), ex);

        URI instance = URI.create(tryToGetRequestUriFromWebRequest(request));
        ApiException apiException = new ApiException(ApiErrorInfo.METHOD_ARGUMENT_NOT_VALID_ERROR);
        ApiProblemDetail problemDetail = ApiProblemDetail.forException(apiException, instance);

        GenericApiResponse<?> response = new GenericApiResponse.Builder<>()
                .isSuccess(false)
                .httpStatusCode(apiException.getExceptionStatus())
                .addErrorResponse(problemDetail)
                .build();
        return new ResponseEntity<>(response, response.httpStatus);
    }


    @ExceptionHandler(value = RuntimeException.class)
    public ResponseEntity<Object> unKnownException(RuntimeException ex, WebRequest request) {
        log.error("unKnown for the following url :: {}", tryToGetRequestUriFromWebRequest(request), ex);
        URI instance = URI.create(tryToGetRequestUriFromWebRequest(request));
        ApiException apiException = new ApiException(ApiErrorInfo.SOMETHING_WENT_WRONG);

        ApiProblemDetail problemDetail = ApiProblemDetail.forException(apiException, instance);

        GenericApiResponse<String> response = new GenericApiResponse.Builder<String>()
                .isSuccess(false)
                .httpStatusCode(apiException.getExceptionStatus())
                .addErrorResponse(problemDetail)
                .build();
        return new ResponseEntity<>(response, response.httpStatus);
    }


    @ExceptionHandler(value = ApiException.class)
    public ResponseEntity<Object> handleApiException(ApiException ex, HttpServletRequest httpServletRequest) {
        URI instance = URI.create(httpServletRequest.getRequestURI());

        ApiProblemDetail problemDetail = ApiProblemDetail.forException(ex, instance);

        GenericApiResponse<String> response = new GenericApiResponse.Builder<String>()
                .isSuccess(false)
                .httpStatusCode(ex.getExceptionStatus())
                .addErrorResponse(problemDetail)
                .build();
        return new ResponseEntity<>(response, ex.getExceptionStatus());
    }
}
