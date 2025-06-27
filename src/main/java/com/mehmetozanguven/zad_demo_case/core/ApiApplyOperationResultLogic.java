package com.mehmetozanguven.zad_demo_case.core;


import com.mehmetozanguven.zad_demo_case.core.exception.ApiException;

// Generic interface to apply any business logic
public interface ApiApplyOperationResultLogic<BeforeRequest, BeforeResponse, ExecuteResponse> {
    default OperationResult<ExecuteResponse> applyBusiness(BeforeRequest beforeRequest) throws ApiException {
        OperationResult<BeforeResponse> beforeResult = logicBefore(beforeRequest);
        if (!beforeResult.isValid()) {
            return OperationResult.<ExecuteResponse>builder().valid(false).addListOfExceptions(beforeResult.getException()).build();
        }
        OperationResult<ExecuteResponse> executeResponse = executeLogic(beforeResult.getReturnedValue());
        afterExecution(executeResponse);
        return executeResponse;
    }

    OperationResult<BeforeResponse> logicBefore(BeforeRequest beforeRequest);
    OperationResult<ExecuteResponse> executeLogic(BeforeResponse beforeResponse);
    void afterExecution(OperationResult<ExecuteResponse> response);
}
