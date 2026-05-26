package com.letraaletra.api.application.usecase;

public interface UseCaseWithoutOutput<Input> {
    void execute(Input command);
}
