package com.chachef.service.exceptions;

public class InternalAppError extends RuntimeException {
    public InternalAppError(String message) {
        super(message);
    }
}
