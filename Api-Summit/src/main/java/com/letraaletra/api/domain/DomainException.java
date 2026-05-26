package com.letraaletra.api.domain;

public class DomainException extends RuntimeException {

    public DomainException(MessageCode messageCode) {
        super(messageCode.getMessage());
    }
}