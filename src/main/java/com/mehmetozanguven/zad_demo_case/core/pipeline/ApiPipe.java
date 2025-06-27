package com.mehmetozanguven.zad_demo_case.core.pipeline;


import com.mehmetozanguven.zad_demo_case.core.OperationResult;

public interface ApiPipe<IN, OUT> {
    OperationResult<OUT> process(IN input);
}
