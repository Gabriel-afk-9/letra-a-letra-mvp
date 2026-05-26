package com.letraaletra.api.application.usecase;

public interface UseCase<Input, Output> {
    Output execute(Input command);
}
