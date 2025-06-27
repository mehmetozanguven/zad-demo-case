package com.mehmetozanguven.zad_demo_case.core.pipeline;


import com.mehmetozanguven.zad_demo_case.core.OperationResult;

public class ApiPipeline<IN, OUT> {
    private final ApiPipe<IN, OUT> current;

    private ApiPipeline(ApiPipe<IN, OUT> current) {
        this.current = current;
    }

    public static <T> ApiPipeline<T, T> start() {
        return new ApiPipeline<>(input -> OperationResult.<T>builder().addReturnedValue(input).build()); // Identity pipe
    }

    public <NewOUT> ApiPipeline<IN, NewOUT> pipe(ApiPipe<OUT, NewOUT> next) {
        return new ApiPipeline<>(input -> {
            OperationResult<OUT> currentResult = current.process(input);
            currentResult.validateResult();
            return next.process(currentResult.getReturnedValue());
        });
    }

    public OUT execute(IN input) {
        OperationResult<OUT> result = current.process(input);
        result.validateResult();
        return result.getReturnedValue();
    }
}
