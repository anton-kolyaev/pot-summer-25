package com.coherentsolutions.pot.insurance_service.exception;

public class InsufficientAuthenticationException extends RuntimeException {
    private final Object details;

    public InsufficientAuthenticationException(String message) {
        super(message);
        this.details = null;
    }
    public InsufficientAuthenticationException(Object details) {
        this.details = details;
    }
    public InsufficientAuthenticationException(String message, Object details) {
        super(message);
        this.details = details;
    }
    public Object getDetails() {
        return details;
    }
}
